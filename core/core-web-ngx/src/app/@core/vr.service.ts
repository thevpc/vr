// @author vpc, vr auth ws
import {Observable} from 'rxjs/Rx';
import {Injectable} from '@angular/core';
import {CurrentUser, VrSharedState} from './vr.shared-state';
import {VrHttp} from "./vr.http";


/**
 * Http REST Client Service Implementation for Vain Ruling WebScript Services.
 * @author vpc (Taha BEN SALAH)
 * @version 1.0
 * @lastModified 2017-12-03
 */
@Injectable()
export class VrService {
  constructor(private model: VrSharedState, private vrHttp: VrHttp) {

  }

  public getCurrentUser(): Observable<any> {
    return this.vrHttp.invokeBeanMethod('core', 'getCurrentUser()').map(
      r => {
        this.model.currentUser.next(new CurrentUser(r.contact.fullName, '', r, true));
        return r;
      }
    );
  }

  public getEntityInfo(entityName: string): Observable<any> {
    return this.getPersistenceUnitInfo().map(v=>{
      return this.findEntityInfo(v,entityName);
    })
  }

  public findEntityInfo(node: any, entityName: string): any {
    if (node.type == 'persistenceUnit') {
      return this.findEntityInfo(node.root, entityName);
    }
    if (node.type == 'package') {
      for (let i = 0; i < node.children.length; i++) {
        let child = node.children[i];
        let found = this.findEntityInfo(child, entityName);
        if (found != null) {
          return found;
        }
      }
      return null;
    }
    if (node.type == 'entity') {
      if (node.name==entityName) {
        return node;
      }
      return null;
    }
    return null;
  }

  // private findSectionInfo(node:any,entityName:string) : any{
  //   if(node.type=='persistenceUnit'){
  //     return this.findEntityInfo(node.root,entityName);
  //   }
  //   if(node.type=='package'){
  //     for(let i=0;i<node.children.length;i++){
  //       let child = node.children[i];
  //       let found=this.findEntityInfo(child,entityName);
  //       if(found != null){
  //         return found;
  //       }
  //     }
  //     return null;
  //   }
  //   if(node.type=='entity'){
  //     if(node.name.equal(node)){
  //       return node;
  //     }
  //     return null;
  //   }
  //   return null;
  // }

  public getPersistenceUnitInfo(): Observable<any> {
    if (this.model.domainModel.getValue() != null && this.model.domainModel.getValue().root) {
      return this.model.domainModel;
    }
    return this.vrHttp.invokeBeanMethod('core', 'getPersistenceUnitInfo()').map(
      r => {
        this.model.domainModel.next(r);
        return r;
      }
    );
  }

  public isValidSession(): boolean {
    return this.model.sessionId != null;
  }

  public authenticate(username: any, password: any): Observable<CurrentUser> {
    return this.vrHttp.authenticate(username, password)
      .map(cu => {
        this.getCurrentUser().subscribe();
        this.getPersistenceUnitInfo().subscribe();
        return cu;
      });
  }

  public logout(): Observable<any> {
    return this.vrHttp.logout();
  }
}
