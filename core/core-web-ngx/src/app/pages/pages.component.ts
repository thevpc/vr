import {Component, OnInit} from '@angular/core';

import {MENU_ITEMS} from './pages-menu';
import {VrService} from "../@core/vr.service";
import {VrSharedState} from "../@core/vr.shared-state";
import {NbMenuItem} from "@nebular/theme";

@Component({
  selector: 'ngx-pages',
  template: `
    <ngx-sample-layout>
      <nb-menu [items]="menu"></nb-menu>
      <router-outlet></router-outlet>
    </ngx-sample-layout>
  `,
})
export class PagesComponent implements OnInit {

  menu = MENU_ITEMS;

  constructor(private vrService: VrService, private vrModemState: VrSharedState) {
  }

  ngOnInit(): void {
    this.vrModemState.domainModel.subscribe(
      newDomain => {
        if (newDomain.root) {
          this.menu = this.rebuildMenu(newDomain);
        }
      },
      err => {
        alert(err);
      }
    );
  }


  private rebuildMenu(domain: any): NbMenuItem[] {
    let r: NbMenuItem[] = [];
    r.push(
      {
        title: 'Dashboard',
        icon: 'nb-home',
        link: '/pages/dashboard',
        home: true,
      }
    );
    if (domain.root) {
      let rootMenu = this.createMenu(domain.root);
      rootMenu.children.forEach((element) => {
        r.push(element);
      });
    }
    r.push(
      {
        title: 'Forms',
        icon: 'nb-compose',
        children: [
          {
            title: 'Form Inputs',
            link: '/pages/forms/inputs',
          },
          {
            title: 'Form Layouts',
            link: '/pages/forms/layouts',
          },
          {
            title: 'Form Entity',
            link: '/pages/forms/entity',
          },
        ],
      }
    );
    r.push(
      {
        title: 'Auth',
        icon: 'nb-locked',
        children: [
          {
            title: 'Login',
            link: '/auth/login',
          },
          {
            title: 'Register',
            link: '/auth/register',
          },
          {
            title: 'Request Password',
            link: '/auth/request-password',
          },
          {
            title: 'Reset Password',
            link: '/auth/reset-password',
          },
        ],
      }
    );
    return r;
  }

  private createMenu(item: any): NbMenuItem {
    let r: NbMenuItem[] = [];
    if (item.type == 'package') {
      item.children.forEach((element) => {
        let rr = this.createMenu(element);
        if (rr != null) {
          r.push(rr);
        }
      });
      if(r.length>0) {
        return {
          title: item.title,
          icon: 'nb-locked',
          children:r
        };
      }else{
        return {
          title: item.title,
          icon: 'nb-locked',
        };
      }
    }else if (item.type == 'entity') {
      item.children.forEach((element) => {
        let rr = this.createMenu(element);
        if (rr != null) {
          r.push(rr);
        }
      });
      if(r.length>0) {
        return {
          title: item.title,
          icon: 'nb-keypad',
          children:r
        };
      }else{
        return {
          title: item.title,
          icon: 'nb-keypad',
          link: '/pages/forms/entity'
        };
      }
    }else if (item.type == 'section') {
      item.children.forEach((element) => {
        let rr = this.createMenu(element);
        if (rr != null) {
          r.push(rr);
        }
      });
      if(r.length>0) {
        return {
          title: item.title,
          icon: 'nb-location',
          children:r
        };
      }else{
        return {
          title: item.title,
          icon: 'nb-location',
        };
      }
    }
    return null;
  }

}
