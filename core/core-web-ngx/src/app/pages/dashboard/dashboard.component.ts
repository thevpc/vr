import {Component, OnInit} from '@angular/core';
import {VrSharedState} from "../../@core/vr.shared-state";

@Component({
  selector: 'ngx-dashboard',
  styleUrls: ['./dashboard.component.scss'],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  constructor(private vrModelState:VrSharedState) {
  }

  ngOnInit(): void {
    this.vrModelState.sessionId.subscribe(r=>{
      this.sessionId=r;
    });
  }

  public sessionId : string;
}
