import {ModuleWithProviders, NgModule, Optional, SkipSelf} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NbAuthModule} from '@nebular/auth';

import {throwIfAlreadyLoaded} from './module-import-guard';
import {DataModule} from './data/data.module';
import {AnalyticsService} from './utils/analytics.service';
import {VrAuthProvider} from './vr.auth-provider';
import {VrService} from './vr.service';
import {VrSharedState} from './vr.shared-state';
import {VrHttp} from "./vr.http";

const NB_CORE_PROVIDERS = [
  ...DataModule.forRoot().providers,
  ...NbAuthModule.forRoot({
    providers: {
      email: {
        service: VrAuthProvider,
        config: {
          delay: 3000,
          login: {
            rememberMe: true,
          },
        },
      },
    },
  }).providers,
  AnalyticsService,
];

@NgModule({
  imports: [
    CommonModule,
  ],
  exports: [
    NbAuthModule,
  ],
  declarations: [],
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');
  }

  static forRoot(): ModuleWithProviders {
    return <ModuleWithProviders>{
      ngModule: CoreModule,
      providers: [
        ...NB_CORE_PROVIDERS,
        // @author vpc, added providers
        VrHttp,
        VrAuthProvider,
        VrSharedState,
        VrService,
      ],
    };
  }
}
