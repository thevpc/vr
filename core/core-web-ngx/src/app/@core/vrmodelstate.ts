// @author vpc, vr auth ws
import {Observable} from 'rxjs/Observable';
import {NbAuthResult} from '../../../node_modules/@nebular/auth/services/auth.service';
import {NbAbstractAuthProvider} from '../../../node_modules/@nebular/auth/providers/abstract-auth.provider';
import {HttpModule, Http, Response, RequestOptions, RequestOptionsArgs, Headers} from '@angular/http';
import {Injectable} from '@angular/core';

export class CurrentUser {
  public name: String = 'Nick Jones';
  public picture: String = 'assets/images/nick.png';
  public obj: any = '';
  public connected: Boolean = false;
}

@Injectable()
export class VrModelState {
  // protected apiRoot = 'http://eniso.info/ws/core';
  public apiRoot = 'http://localhost:8080/ws/core';
  public sessionId;
  public currentUser: CurrentUser =  {name: 'Nick Jones', picture: 'assets/images/nick.png', obj: '', connected : false};
  constructor(private http: Http) {

  }

  getCurrentUser() {
    return Observable.of(this.currentUser);
  }

}
