// @author vpc, vr auth ws
import {Observable} from 'rxjs/Rx';
import {Headers, Http, RequestOptionsArgs} from '@angular/http';
import {Injectable} from '@angular/core';
import {Credentials, CurrentUser, VrSharedState} from './vr.shared-state';

/**
 * Http REST Client Raw/Http Implementation for Vain Ruling WebScript Services.
 * @author vpc (Taha BEN SALAH)
 * @version 1.0
 * @lastModified 2017-12-03
 */
@Injectable()
export class VrHttp {

  constructor(private http: Http, private model: VrSharedState) {

  }

  /**
   *
   * @returns {boolean} true if sessionId is not null!
   */
  public isValidSession(): boolean {
    return this.model.sessionId != null;
  }

  /**
   * makes an authentication call and stores sessionId for future use.
   * @param username
   * @param password
   * @returns {Observable<CurrentUser>}
   */
  public authenticate(username: any, password: any): Observable<CurrentUser> {
    if (username == null || username == '' || password == null || password == '') {
      return this.throwSecurityException();
    }
    const apiURL = `${this.model.apiRoot}/login?app=ng&login=${username}&password=${password}`;
    //alert('authenticate : ' + apiURL);
    return this.http.get(apiURL).map(res => {
      const jsessionid = res.headers.get('x-jsessionid');
      //alert('found session : ' + jsessionid);
      if (jsessionid == null) {
        return;
      }
      this.model.sessionId.next(jsessionid);
      const item = res.json();
      let ret: CurrentUser;
      if (item['$1'] != null) {
        this.model.currentUser.next(ret = new CurrentUser(item['$1'].userFullName, '', item['$1'], true));
        this.model.credentials = new Credentials(username, password);
        // alert('received okkay ' + JSON.stringify(item['$1']));
      } else if (item['$error'] != null) {
        this.model.currentUser.next(ret = new CurrentUser('Unknown', '', null, false));
        // alert('received error ' + JSON.stringify(item['$error']));
      } else {
        // alert('received error2 ' + JSON.stringify(item));
        this.model.currentUser.next(ret = new CurrentUser('Unknown', '', null, false));
      }
      return ret;
    }).catch(error2 => {
      const item = error2;
      if (item['$error'] != null) {
        return this.throwObj(item['$error']);
      } else {
        return this.throwMsg('UnknownError', 'Unknown error : ' + this._str(error2));
      }
    });
  }

  /**
   * Log out from the server.
   * Client is supposed to be already authenticated successfully;
   * If not a Security Exception will be thrown.
   * @returns {Observable<any>}
   */
  public logout(): Observable<any> {
    return this.authenticatedCall(`${this.model.apiRoot}/logout`);
  }


  /**
   * Executes bean method on the server.
   * If Client is not authenticated, a call to authenticate is done implicitly;
   * @param {string} beanName
   * @param {string} methodCall
   * @returns {Observable<any>}
   */
  public invokeBeanMethod(beanName: string, methodCall: string): Observable<any> {
    return this.invokeWScript('Return(bean(\'' + beanName + '\').' + methodCall + ')');
  }

  /**
   * Executes a Web Script on the server.
   * If Client is not authenticated, a call to authenticate is done implicitly;
   * @param {string} script
   * @returns {Observable<any>}
   */
  public invokeWScript(script: string): Observable<any> {
    if (this.isValidSession()) {
      //alert('Found a valid Session : '+script);
      return this.authenticatedWScript(script).catch(err => {
        //alert('Not a so valid Session  : '+script+JSON.stringify(err));
        if (this.model.credentials == null) {
          return this.throwSecurityException();
        }
        return this
          .authenticate(this.model.credentials.name, this.model.credentials.pwd)
          .concat(this.authenticatedWScript(script));
      });
    } else {
      if (!this.model.credentials) {
        return this.throwSecurityException();
      }
      return this
        .authenticate(this.model.credentials.name, this.model.credentials.pwd)
        .concat(this.authenticatedWScript(script));
    }
  }

  /**
   * Executes a Web Script on the server.
   * Client is supposed to be already authenticated successfully;
   * If not a Security Exception will be thrown.
   * @param {string} script
   * @returns {Observable<any>}
   */
  public authenticatedWScript(script: string): Observable<any> {
    const apiURL = `${this.model.apiRoot}/wscript?s=${script}`;
    return this.authenticatedCall(apiURL);
  }

  /**
   * Calls VR REST webservice and returns the result.
   * Client is supposed to be already authenticated successfully;
   * If not a Security Exception will be thrown.
   * @param {string} apiURL url to call
   * @returns {Observable<any>} result of type {$1:any, $2:ay,...} if success ; otherwise will throw an error of type {type:string,message:string}
   */
  public authenticatedCall(apiURL: string): Observable<any> {
    let sessionId = this.model.sessionId.getValue();
    if (sessionId == null || sessionId == '') {
      //i'm not connected yet, no need to spoiler the server;
      return this.throwSecurityException();
    }
    //alert('authenticatedCall ' + apiURL+" ## "+ sessionId);

    const reqHeaders = new Headers();
    reqHeaders.append('X-JSESSIONID', sessionId);
    const basicOptions: RequestOptionsArgs = {
      withCredentials: true,
      headers: reqHeaders,
      body: null,
    };
    return this.http.get(apiURL, basicOptions).map(res => {
      const jsessionid = res.headers.get('x-jsessionid');
      //alert('found session : ' + jsessionid);
      if (jsessionid != null && jsessionid != this.model.sessionId.getValue()) {
        this.model.sessionId.next(jsessionid);
      }

      let rj = res.json();
      if (rj['$error'] == null) {
        return rj['$1'];
      } else {
        return this.throwObj(rj['$error']);
      }
    }).catch(error => {
      const item = error;
      if (item['$error'] != null) {
        return this.throwObj(item['$error']);
      } else {
        return this.throwMsg('UnknownError', 'Unknown error for call authenticatedCall(' + apiURL + ') : ' + JSON.stringify(error));
      }
    });
  }


  private _str(a: any): string {
    if (typeof a === "string") {
      return '' + a;
    }
    let ss = JSON.stringify(a);
    if (ss == '{}') {
      //alert(typeof(a)+'  '+(a.toString != null));
      ss = '' + a;
    }
    return ss;
  }

  private throwObj(a: any): Observable<any> {
    //alert('Error : '+JSON.stringify(a));
    return Observable.throw(a);
  }

  private throwSecurityException(): Observable<any> {
    return this.throwMsg('FastSecurityException', 'Not Connected...');
  }

  private throwMsg(t: string, m: string): Observable<any> {
    return this.throwObj({type: t, message: m});
  }


}
