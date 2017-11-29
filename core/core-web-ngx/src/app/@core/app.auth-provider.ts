// @author vpc, vr auth ws
import {Observable} from 'rxjs/Observable';
import {NbAuthResult} from '../../../node_modules/@nebular/auth/services/auth.service';
import {NbAbstractAuthProvider} from '../../../node_modules/@nebular/auth/providers/abstract-auth.provider';
import {HttpModule, Http, Response} from '@angular/http';
import {Injectable} from '@angular/core';
import {VrService} from './vrservice';
import {NbMenuItem} from '@nebular/theme';
export interface NbAppAuthProviderConfig {
  delay?: number;
  alwaysFail?: boolean;
}


@Injectable()
export class NbAppAuthProvider extends NbAbstractAuthProvider {
  protected defaultConfig: NbAppAuthProviderConfig;
  constructor(private vrService: VrService) {
    super();
  }
  authenticate(data?: any): Observable<NbAuthResult> {
    return this.vrService.authenticate(data.email, data.password)
      .map(currentUser => {
        if ( currentUser.connected ) {
          return new NbAuthResult(true, this.createSuccessResponse(currentUser), '/', ['Successfully logged in.']);
        } else {
          return new NbAuthResult(false, this.createFailResponse(data), null, ['Something went wrong.']);
        }
      });

  // , err => {
  //     alert('ERR : ' + JSON.stringify(err.json()));
  //   }
  }

  register(data?: any): Observable<NbAuthResult> {
    const nbAuthResultOk = new NbAuthResult(true, this.createSuccessResponse(data), '/', ['Successfully logged in.']);
    return Observable.of(nbAuthResultOk);
  }

  requestPassword(data?: any): Observable<NbAuthResult> {
    const nbAuthResultOk = new NbAuthResult(true, this.createSuccessResponse(data), '/', ['Successfully logged in.']);
    return Observable.of(nbAuthResultOk);
  }

  resetPassword(data?: any): Observable<NbAuthResult> {
    const nbAuthResultOk = new NbAuthResult(true, this.createSuccessResponse(data), '/', ['Successfully logged in.']);
    return Observable.of(nbAuthResultOk);
  }

  logout(data?: any): Observable<NbAuthResult> {
    return this.vrService.logout().map(res => new NbAuthResult(true, this.createSuccessResponse(data), '/', ['Successfully logged out.']));
  }

  protected createDummyResult(data?: any): NbAuthResult {
    const nbAuthResultOk = new NbAuthResult(true, this.createSuccessResponse(data), '/', ['Successfully logged in.']);
    return (nbAuthResultOk);
  }

}
