import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {AsyncLocalStorage} from "angular-async-local-storage";
import {Observable} from "rxjs/Observable";

export class PersistentBehaviorSubject<T> extends BehaviorSubject<T> {
  private _storage: AsyncLocalStorage;
  private name: string;
  private defaultValue: T;


  public constructor(name: string, value: T) {
    super(value==null?<T>{}:value);
    this.name = name;
    this.defaultValue = value;
  }

  public get storage(): AsyncLocalStorage {
    return this._storage;
  }

  public set storage(value: AsyncLocalStorage) {
    this._storage = value;
  }

  public store(): Observable<boolean> {
    return this.storage.setItem(this.name, this.getValue());
  }

  public load(): Observable<T> {
    return this.storage.getItem(this.name).map(v => {
      this.next(v);
      return v;
    });
  }

  public getOrReload(forceReload:boolean, o : Observable<T>):Observable<T>{
    if(!forceReload) {
      let v = this.getValue();
      if(v!=this.defaultValue){
        return Observable.of(v);
      }
      return this.load();
    }
    return o.map(m=>{
      return this.update(m);
    });
  }

  public update(t: T) : T{
    this.next(t);
    this.store().subscribe(t=>{});
    return t;
  }

  public reset() {
    this.next(this.defaultValue);
  }

  public next(v:T) {
    if(v==null){
      v=this.createDefaultValue();
    }
    // alert('next '+JSON.stringify(v));
    super.next(v);
  }

  private createDefaultValue() : T{
    const v: T = <T>{};
    return v;
  }

}
