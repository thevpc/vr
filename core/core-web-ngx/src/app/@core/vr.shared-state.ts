// @author vpc, vr auth ws
import {Injectable, OnInit} from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {PersistentBehaviorSubject} from "./PersistentBehaviorSubject";
import {AsyncLocalStorage} from "angular-async-local-storage";

/**
 * Application Shared State. This state should be persisted (Redux,...) to overcome an F5/Refresh invocation!
 * @author vpc (Taha BEN SALAH)
 * @version 1.0
 * @lastModified 2017-12-03
 */
export class CurrentUser {
  public name: String = 'Nick Jones';
  public picture: String = 'assets/images/nick.png';
  public obj: any = '';
  public connected: Boolean = false;

  constructor(name: String, picture: String, obj: any, connected: Boolean) {
    this.name = name;
    this.picture = picture;
    this.obj = obj;
    this.connected = connected;
  }
}

export class Credentials {
  public name: String = null;
  public pwd: String = null;

  constructor(name: String, pwd: String) {
    this.name = name;
    this.pwd = pwd;
  }
}

@Injectable()
export class VrSharedState{
  //public apiRoot: string = 'http://eniso.info/ws/core';
  public apiRoot: string = 'http://localhost:8080/ws/core';
  public sessionId: BehaviorSubject<string> = new BehaviorSubject<string>('');
  public credentials: Credentials;
  public currentUser: BehaviorSubject<CurrentUser> = new BehaviorSubject<CurrentUser>({
    name: '',
    obj: '',
    picture: '',
    connected: false
  });
  public domainModel: PersistentBehaviorSubject<any> = new PersistentBehaviorSubject<any>('domainModel', {});


  constructor(protected storage: AsyncLocalStorage) {
    this.domainModel.storage = this.storage;
  }

  public reset() {
    this.domainModel.next(null);
  }
}
