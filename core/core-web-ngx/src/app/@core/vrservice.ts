// @author vpc, vr auth ws
import {Observable} from 'rxjs/Observable';
import {Headers, Http, RequestOptionsArgs, Response} from '@angular/http';
import {Injectable} from '@angular/core';
import {CurrentUser, VrModelState} from './vrmodelstate';


@Injectable()
export class VrService {
  // protected apiRoot = 'http://eniso.info/ws/core';
  constructor(private http: Http, private model: VrModelState) {

  }


  getCurrentUser() {
    return Observable.of(this.model.currentUser);
  }

  loadCurrentUser(): Observable<any> {
    const s = 'Return(bean(\'core\').getCurrentUser())';
    return this.wscript(s);
  }

  authenticate(username: any, password: any): Observable<CurrentUser> {
    const apiURL = `${this.model.apiRoot}/login?app=ng&login=${username}&password=${password}`;
    alert('authenticate : ' + apiURL);
    return this.http.get(apiURL).map(res => {
      const jsessionid = res.headers.get('x-jsessionid');
      alert('found session : ' + jsessionid);
      if (jsessionid == null) {
        return;
      }
      this.model.sessionId = jsessionid;
      const item = res.json();
      if (item['$1'] != null) {
        this.model.currentUser.name = item['$1'].fullName;
        this.model.currentUser.connected = true;
        this.model.currentUser.obj = item['$1'];
        // alert('received okkay ' + JSON.stringify(item['$1']));
      } else if (item['$error'] != null) {
        this.model.currentUser.name = 'Unknown';
        this.model.currentUser.obj = null;
        this.model.currentUser.connected = false;
        // alert('received error ' + JSON.stringify(item['$error']));
      } else {
        // alert('received error2 ' + JSON.stringify(item));
        this.model.currentUser.name = 'Unknown';
        this.model.currentUser.obj = null;
        this.model.currentUser.connected = false;
      }
      this.loadCurrentUser().subscribe(res2 => {
          alert('Got Some thing... ' + JSON.stringify(res2.json()));
        },
        error2 => {
          alert('ERROR => ' + JSON.stringify(error2));
        });
      return this.model.currentUser;
    }).catch(error => {
      const item = error.json();
      if (item['$error'] != null) {
        return Observable.throw(item['$error']);
      } else {
        return Observable.throw({message: 'Unknown error'});
      }
    });
  }

  logout(): Observable<Response> {
    return this.execAuthenticatedCall(`${this.model.apiRoot}/logout`);
  }

  public wscript(script: string): Observable<any> {
    const apiURL = `${this.model.apiRoot}/wscript?s=${script}`;
    return this.execAuthenticatedCall(apiURL);
  }

  public execAuthenticatedCall(apiURL: string): Observable<any> {
    alert('execAuthenticatedCall ' + apiURL);
    const reqHeaders = new Headers();
    reqHeaders.append('X-JSESSIONID', this.model.sessionId);
    const basicOptions: RequestOptionsArgs = {
      withCredentials: true,
      headers: reqHeaders,
      body: null,
    };
    return this.http.get(apiURL, basicOptions).map(res => {
      alert('RESULT : ' + JSON.stringify(res.json()));
      return res.json();
    }).catch(error => {
      const item = error.json();
      if (item['$error'] != null) {
        return Observable.throw(item['$error']);
      } else {
        return Observable.throw({message: 'Unknown error'});
      }
    });
  }
}
