import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {VrSharedState} from "../../../@core/vr.shared-state";
import {VrService} from '../../../@core/vr.service';
import {NbThemeService} from '@nebular/theme';
import 'ckeditor';
import '../../editors/ckeditor/ckeditor.loader';
import {ActivatedRoute, ParamMap,Router,NavigationExtras} from "@angular/router";
import { Location } from '@angular/common';

import {STRING_TYPE} from "@angular/compiler/src/output/output_ast";

export class FormEntitySectionRow {
  title: string = 'Empty Row';
  cols: string = 'col-md-12';
  children: FormEntitySectionColumn[] = [];
}

export class FormEntitySectionColumn {
  title: string = 'Empty Column';
  children: FormEntitySectionPanel[] = [];
}

export class FormEntitySectionPanel {
  title: string = 'Empty Panel';
  children: FormEntityField[] = [];

}

export class FormEntityField {
  title: string = 'No Field Title';
  name: string = 'No Field Name';
  effectivePersistAccessLevel: string;
  simpleProperties: string;
  dataType: any;
  manyToOne: boolean;
  system: boolean;
  enabled: boolean = true;
  control: FControl = FControl.TEXT;
  vals: any;
}

export enum EditorMode {
  PERSIST,
  UPDATE,
  READ
}

export enum FControl {
  TEXT,
  TEXTAREA,
  EDITOR,
  CHECKBOX,
  DROPDOWNLIST,
  RATING,
  FILE,
}

export class FormEntityConfig {

  id:string;
  listFilter:string ;
  values:{};
  disabledFields:string[];
  selectedFields:string[];
  searchExpr:string;
  ignoreAutoFilter:string;
}

@Component({
  selector: 'ngx-form-entity',
  styleUrls: ['./form-entity.component.scss'],
  templateUrl: './form-entity.component.html',
})


export class FormEntityComponent implements OnInit {
  public entityName: string = 'AppUser';
  public entityInfo: any = {};
  public editorRows: FormEntitySectionRow[] = [];
  public editorMode: EditorMode = EditorMode.PERSIST;
  public editorConfig:FormEntityConfig=new FormEntityConfig();
  public objs:any={};

  themeName = 'default';
  settings: Array<any>;
  themeSubscription: any;
  buttonsViews = [{
    title: 'Default Buttons',
    key: 'default',
  }, {
    title: 'Outline Buttons',
    key: 'outline',
  }];

  constructor(private vrService: VrService,
              private vrSharedModel: VrSharedState,
              private themeService: NbThemeService,
              private route: ActivatedRoute,
              private location: Location,
              private router: Router) {
    this.themeSubscription = this.themeService.getJsTheme().subscribe(theme => {
      this.themeName = theme.name;
      this.init(theme.variables);
    });
  }


  init(colors: any) {
    this.settings = [];
    this.settings = [{
      class: 'btn-hero-primary',
      container: 'primary-container',
      title: 'Primary Button',
      buttonTitle: 'Enregistrer',
      default: {
        gradientLeft: `adjust-hue(${colors.primary}, 20deg)`,
        gradientRight: colors.primary,
      },
      cosmic: {
        gradientLeft: `adjust-hue(${colors.primary}, 20deg)`,
        gradientRight: colors.primary,
        bevel: `shade(${colors.primary}, 14%)`,
        shadow: 'rgba (6, 7, 64, 0.5)',
        glow: `adjust-hue(${colors.primary}, 10deg)`,
      },
    }, {
      class: 'btn-hero-warning',
      container: 'warning-container',
      title: 'Warning Button',
      buttonTitle: 'Nouveau',
      default: {
        gradientLeft: `adjust-hue(${colors.warning}, 10deg)`,
        gradientRight: colors.warning,
      },
      cosmic: {
        gradientLeft: `adjust-hue(${colors.warning}, 10deg)`,
        gradientRight: colors.warning,
        bevel: `shade(${colors.warning}, 14%)`,
        shadow: 'rgba (33, 7, 77, 0.5)',
        glow: `adjust-hue(${colors.warning}, 5deg)`,
      },
    }, {
      class: 'btn-hero-success',
      container: 'success-container',
      title: 'Rafraichir',
      buttonTitle: 'Rafraichir',
      default: {
        gradientLeft: `adjust-hue(${colors.success}, 20deg)`,
        gradientRight: colors.success,
      },
      cosmic: {
        gradientLeft: `adjust-hue(${colors.success}, 20deg)`,
        gradientRight: colors.success,
        bevel: `shade(${colors.success}, 14%)`,
        shadow: 'rgba (33, 7, 77, 0.5)',
        glow: `adjust-hue(${colors.success}, 10deg)`,
      },
    }, {
      class: 'btn-hero-info',
      container: 'info-container',
      title: 'Liste',
      buttonTitle: 'Liste',
      default: {
        gradientLeft: `adjust-hue(${colors.info}, -10deg)`,
        gradientRight: colors.info,
      },
      cosmic: {
        gradientLeft: `adjust-hue(${colors.info}, -10deg)`,
        gradientRight: colors.info,
        bevel: `shade(${colors.info}, 14%)`,
        shadow: 'rgba (33, 7, 77, 0.5)',
        glow: `adjust-hue(${colors.info}, -5deg)`,
      },
    }, {
      class: 'btn-hero-danger',
      container: 'danger-container',
      title: 'Danger Button',
      buttonTitle: 'Supprimer',
      default: {
        gradientLeft: `adjust-hue(${colors.danger}, -20deg)`,
        gradientRight: colors.danger,
      },
      cosmic: {
        gradientLeft: `adjust-hue(${colors.danger}, -20deg)`,
        gradientRight: colors.danger,
        bevel: `shade(${colors.danger}, 14%)`,
        shadow: 'rgba (33, 7, 77, 0.5)',
        glow: `adjust-hue(${colors.danger}, -10deg)`,
      },
    }, {
      class: 'btn-hero-secondary',
      container: 'secondary-container',
      title: 'Ghost Button',
      buttonTitle: 'Recalculer',
      default: {
        border: '#dadfe6',
      },
      cosmic: {
        border: colors.primary,
        bevel: '#665ebd',
        shadow: 'rgba (33, 7, 77, 0.5)',
        glow: 'rgba (146, 141, 255, 1)',
      },
    }];
  }

  ngOnDestroy() {
    this.themeSubscription.unsubscribe();
  }


  ngOnInit(): void {
   // alert('jamais...');
    this.route.queryParamMap.subscribe(pm=>{
      let v = pm.get('object');
      if(v) {
      //  console.log(v); // v = {id:'1' , name: 'manel'}
        this.editorConfig = Object.assign(new FormEntityConfig(), JSON.parse(v));
      }
    });
    this.route.paramMap
      .map((p: ParamMap) => {
        let v = p.get('name');
      //  alert('name='+v);
        this.entityName = v;
        this.vrService.getPersistenceUnitInfo().subscribe(s =>{
          if(s && s.type) {
            this.entityInfo = this.vrService.findEntityInfo(s, this.entityName);
            this.onChangedEntityInfo();
          }
        });
      }).subscribe();
  }


  private createFormEntityField(field): FormEntityField {
    var f = new FormEntityField();
    f.name = field.name;
    f.title = field.title;

    f.dataType = field.dataType;
    // console.log(JSON.stringify(field.dataType.type));
    f.effectivePersistAccessLevel = field.effectivePersistAccessLevel;
    f.simpleProperties = field.simpleProperties;
    f.system = field.system;
   //  console.log('system = '+f.system);
    //  field.info.effectiveReadAccessLevel
    // if (field.info.main || field.info.summary ){
    // }

    if (field.system) {
      return null;
    }
    let accessLevel = this.editorMode == EditorMode.PERSIST ? field.effectivePersistAccessLevel : this.editorMode == EditorMode.UPDATE ? field.effectiveUpdateAccessLevel : field.effectiveReadAccessLevel;
    if (accessLevel == "INACCESSIBLE") {
      return null;
    } else if (accessLevel == "READ_ONLY") {
      f.enabled = false;
    } else if (accessLevel == "READ_WRITE") {
      f.enabled = true;
    }
    // if(this.editorConfig.disabledFields.indexOf(f.name)>=0){
    //   f.enabled=false;
    // }
    switch (field.dataType.type) {
      case "net.vpc.upa.types.StringType":
      case "net.vpc.upa.types.IntType":
      case "net.vpc.upa.types.LongType":
      case "net.vpc.upa.types.DoubleType":
      case "net.vpc.upa.types.DateTimeType":
      case "net.vpc.upa.types.TimestampType":{
        f.control = FControl.TEXT;
       // alert( "yes text");
        break;
      }

      case "net.vpc.upa.types.EnumType" :
      case "net.vpc.upa.types.ManyToOneType": {
        f.control = FControl.DROPDOWNLIST;
        break;
      }
      case "net.vpc.upa.types.BooleanType" : {
        f.control = FControl.CHECKBOX;
        break;
      }
    }

    if (field.manyToOne) {
      f.control = FControl.DROPDOWNLIST;
    }
    //console.log(JSON.stringify(field.simpleProperties));
    if (field.simpleProperties["ui.form.control"] != null) {
     var result = FControl[<string>(field.simpleProperties["ui.form.control"])];
      if ( result == "rating" ){
        f.control= FControl.RATING;
      } else if ( result == "file" ){
        f.control= FControl.FILE;
      } else if ( result == "richtextarea" ){
        f.control= FControl.EDITOR;
      }else {
        f.control= FControl.TEXTAREA ;
      }

    }

    if (f.control == FControl.DROPDOWNLIST) {
     this.vrService.getSelectList(this.entityName, f.name, null, null).subscribe(val => {
       f.vals = val
          // console.log('these are fvalues = ' + JSON.stringify(f.values));
       console.log("F values = "+JSON.stringify(f.vals));
       }
     );
    }
    //f.vals = field.vals ;
   // console.log('Types = '+JSON.stringify(f.dataType.type));
    return f;
  }

  private createFormEntitySectionRows(entityInfo): FormEntitySectionRow[] {
    var rows: FormEntitySectionRow[] = [];
    var emptyRow: FormEntitySectionRow = null;
    var emptyColumn: FormEntitySectionColumn = null;
    var emptySection: FormEntitySectionPanel = null;
    if (entityInfo.children) {
      for (let obj of entityInfo.children) {
        if (obj.type == 'section') {
          let row = this.createFormEntitySectionRow(obj);
        //  console.log ('the row under create section rowS = '+JSON.stringify(row));
          if (row != null && row.children.length > 0) {
            rows.push(row);
          }
        } else {
          if (emptyRow == null) {
            emptyRow = new FormEntitySectionRow();
            emptyColumn = new FormEntitySectionColumn();
            emptyRow.children.push(emptyColumn);
            emptySection = new FormEntitySectionPanel();
            emptyColumn.title = 'No Section';
            emptyColumn.children.push(emptySection);

          }

          let field = this.createFormEntityField(obj);
          if (field != null) {
            if (emptySection.children.length == 0) {
              rows.push(emptyRow);
            }
            emptySection.children.push(field);
          }
        }
      }
      if (rows.length == 1 && emptySection != null) {
        //this section contains no sub sections!
        emptySection.title = 'Général';
      }

      // the following code devides the cells
      for (let obj of rows) {
        let cols = 12 / obj.children.length;
        obj.cols = 'col-md-' + Math.ceil(cols);
      }
    }
   // console.log('createFormEntitySectionRows result '+JSON.stringify(rows));
    return rows;
  }

  private createFormEntitySectionRow(sectionInfo) {
    // let ss=this.countSubSections(section);
    let currentRow: FormEntitySectionRow = new FormEntitySectionRow();
    var emptyColumn: FormEntitySectionColumn = null;
    var emptySection: FormEntitySectionPanel = null;
    for (let obj of sectionInfo.children) {
      if (obj.type == 'section') {
        let col = this.createFormEntitySectionColumn(obj);
       // console.log ('the cols under create section row = '+JSON.stringify(col));
        if (col != null && col.children.length > 0) {
          currentRow.children.push(col);
        }
        //new FormEntitySectionRow()
      } else {
        //alert('createFormEntitySectionRow why '+obj.type);
        if (emptyColumn == null) {
          emptyColumn = new FormEntitySectionColumn();
          emptySection = new FormEntitySectionPanel();
          emptyColumn.children.push(emptySection);

        }
        let field = this.createFormEntityField(obj);
        if (field != null) {
          if (currentRow.children.length == 0) {
            currentRow.children.push(emptyColumn);
          }
          emptySection.children.push(field);
        }

      }
    }
    if (currentRow.children.length == 1 && emptySection != null) {
      //this section contains no sub sections!
      emptySection.title = sectionInfo.title;
    }
    if (currentRow.children.length == 0) {
      return null;

    }
   // console.log('createFormEntitySectionRow result '+JSON.stringify(currentRow));
    return currentRow;
  }

  private createFormEntitySectionColumn(sectionInfo): FormEntitySectionColumn {
    let currentColumn: FormEntitySectionColumn = new FormEntitySectionColumn();
    var emptySection: FormEntitySectionPanel = null;
    for (let obj of sectionInfo.children) {
      if (obj.type == 'section') {
        let section = this.createFormEntitySectionPanel(obj);
        if (section != null && section.children.length > 0) {
          currentColumn.children.push(section);
        }
        //new FormEntitySectionRow()
      } else {
        if (emptySection == null) {
          emptySection = new FormEntitySectionPanel();

        }
        let field = this.createFormEntityField(obj);
        if (field != null) {
          if (emptySection.children.length == 0) {
            currentColumn.children.push(emptySection);
          }
          emptySection.children.push(field);
        }
      }
    }
    if (currentColumn.children.length == 1 && emptySection != null) {
      //this section contains no sub sections!
      emptySection.title = sectionInfo.title;
    }
    if (currentColumn.children.length == 0) {
    //  return currentColumn; // houni mochkla
    return null ;
    }
    return currentColumn;
  }

  private flattenFields(section, buffer: any[]): any[] {
    var t: any[] = [];
    for (let obj of section.children) {
      if (obj.type == 'section') {
        t = this.flattenFields(obj, t);
      } else {
        t.push(obj);
      }
    }
    return t;
  }

  private createFormEntitySectionPanel(sectionInfo): FormEntitySectionPanel {
   // alert('createFormEntitySectionPanel ' + sectionInfo.name);
    let currentPanel: FormEntitySectionPanel = new FormEntitySectionPanel();
    currentPanel.title = sectionInfo.title;
    for (let obj of sectionInfo.children) {
      if (obj.type == 'section') {
        var t: any[] = [];
        //alert('before flattenFields '+sectionInfo.name);
        this.flattenFields(obj, t).forEach((element) => {
          let field = this.createFormEntityField(element);
          if (field != null) {
            currentPanel.children.push(field);
          }

        });
        //new FormEntitySectionRow()
      } else {
        let field = this.createFormEntityField(obj);
        if (field != null) {
          currentPanel.children.push(field);
        }
      }
    }
    if (currentPanel.children.length == 0) {
      return null;
    }
    return currentPanel;
  }

  private onChangedEntityInfo() {
    //alert('updateRows '+JSON.stringify(this.entityInfo));
    console.clear();
    console.log(this.entityInfo);
    this.editorRows = this.createFormEntitySectionRows(this.entityInfo);
  //  console.log(' HERE '+JSON.stringify(this.editorRows));
  }


  // NOT Really Needed!

  // private countSectionDepth(sectionInfo): number {
  //   var count = 1;
  //   for (let obj of sectionInfo.children) {
  //     if (obj.type == 'section') {
  //       var t = 1 + this.countSectionDepth(obj);
  //       if (t > count) {
  //         count = t;
  //       }
  //     } else {
  //       var t = 1 + 1;
  //       if (t > count) {
  //         count = t;
  //       }
  //     }
  //   }
  //   return count;
  // }
  //
  // private countSubSections(sectionInfo): number {
  //   let nbr = 0;
  //   let nbrExtra = 0;
  //   for (let obj of sectionInfo.children) {
  //     if (obj.type == 'section') {
  //       nbr++;
  //     } else {
  //       nbrExtra = 1;
  //     }
  //   }
  //   return nbrExtra + nbr;
  // }

}
