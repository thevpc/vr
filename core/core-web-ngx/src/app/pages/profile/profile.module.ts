import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SmartTableService } from '../../@core/data/smart-table.service';
import { ProfileComponent } from './profile.component';
import { ThemeModule } from '../../@theme/theme.module';
import { Ng2SmartTableModule } from 'ng2-smart-table';
import {routes} from "@nebular/auth/auth.routes";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ThemeModule,
    Ng2SmartTableModule
    // routes
  ],
  declarations: [
    ProfileComponent
  ],
  providers: [
    SmartTableService
  ]
})
export class ProfileModule {}
