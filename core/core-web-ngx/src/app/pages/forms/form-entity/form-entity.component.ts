import {Component, OnInit} from '@angular/core';
import {VrSharedState} from "../../../@core/vr.shared-state";
import {VrService} from "../../../@core/vr.service";
import {NbThemeService} from "@nebular/theme";

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
  info: any;
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
  constructor(private vrService: VrService, private vrSharedModel: VrSharedState,private themeService: NbThemeService) {
    this.themeSubscription = this.themeService.getJsTheme().subscribe(theme => {
      this.themeName = theme.name;
      this.init(theme.variables);
    });
  }



  init(colors: any) {
    this.settings= [];
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
    this.vrService.getPersistenceUnitInfo().subscribe(s => {
      this.entityInfo = this.vrService.findEntityInfo(s, this.entityName);
      this.onChangedEntityInfo();
    })
  }


  private createFormEntityField(field): FormEntityField {
    var f = new FormEntityField();
    f.name = field.name;
    f.title = field.title;
    //f.info = field;
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
          rows.push(this.createFormEntitySectionRow(obj));
        } else {
          // alert('createFormEntitySectionRows why '+obj.type);
          if (emptyRow == null) {
            emptyRow = new FormEntitySectionRow();

            emptyColumn = new FormEntitySectionColumn();
            emptyRow.children.push(emptyColumn);

            emptySection = new FormEntitySectionPanel();
            emptyColumn.title='No Section';
            emptyColumn.children.push(emptySection);

            rows.push(emptyRow);
          }
          emptySection.children.push(this.createFormEntityField(obj));
        }
      }

      if(rows.length==1 && emptySection!=null){
        //this section contains no sub sections!
        emptySection.title='Général';
      }

      for (let obj of rows) {
        let cols = 12/obj.children.length;
        obj.cols= 'col-md-'+Math.ceil(cols);
      }
    }
    //alert('createFormEntitySectionRows result '+JSON.stringify(rows));
    return rows;
  }

  private createFormEntitySectionRow(sectionInfo) {
    // let ss=this.countSubSections(section);
    let currentRow: FormEntitySectionRow = new FormEntitySectionRow();
    var emptyColumn: FormEntitySectionColumn = null;
    var emptySection: FormEntitySectionPanel = null;
    for (let obj of sectionInfo.children) {
      if (obj.type == 'section') {
        currentRow.children.push(this.createFormEntitySectionColumn(obj));
        //new FormEntitySectionRow()
      } else {
        //alert('createFormEntitySectionRow why '+obj.type);
        if (emptyColumn == null) {
          emptyColumn = new FormEntitySectionColumn();
          currentRow.children.push(emptyColumn);
          emptySection = new FormEntitySectionPanel();
          emptyColumn.children.push(emptySection);
        }
        emptySection.children.push(this.createFormEntityField(obj));
      }
    }
    if(currentRow.children.length==1 && emptySection!=null){
      //this section contains no sub sections!
      emptySection.title=sectionInfo.title;
    }
    return currentRow;
  }

  private createFormEntitySectionColumn(sectionInfo): FormEntitySectionColumn {
    let currentColumn: FormEntitySectionColumn = new FormEntitySectionColumn();
    var emptySection: FormEntitySectionPanel = null;
    for (let obj of sectionInfo.children) {
      if (obj.type == 'section') {
        currentColumn.children.push(this.createFormEntitySectionPanel(obj));
        //new FormEntitySectionRow()
      } else {
        //alert('createFormEntitySectionColumn why '+obj.type);
        if (emptySection == null) {
          emptySection = new FormEntitySectionPanel();
          currentColumn.children.push(emptySection);
        }
        emptySection.children.push(this.createFormEntityField(obj));
      }
    }
    if(currentColumn.children.length==1 && emptySection!=null){
      //this section contains no sub sections!
      emptySection.title=sectionInfo.title;
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
    alert('createFormEntitySectionPanel '+sectionInfo.name);
    let currentPanel: FormEntitySectionPanel = new FormEntitySectionPanel();
    currentPanel.title=sectionInfo.title;
    for (let obj of sectionInfo.children) {
      if (obj.type == 'section') {
        var t: any[] = [];
        //alert('before flattenFields '+sectionInfo.name);
        this.flattenFields(obj, t).forEach((element) => {
          currentPanel.children.push(this.createFormEntityField(element));
        });
        //new FormEntitySectionRow()
      } else {
        currentPanel.children.push(this.createFormEntityField(obj));
      }
    }
    return currentPanel;
  }

  private onChangedEntityInfo() {
    //alert('updateRows '+JSON.stringify(this.entityInfo));
    console.clear();
    console.log(this.entityInfo);
    this.editorRows = this.createFormEntitySectionRows(this.entityInfo);
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
