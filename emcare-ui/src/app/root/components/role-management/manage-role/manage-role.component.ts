import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { RoleManagementService } from 'src/app/root/services/role-management.service';
import { ToasterService } from 'src/app/shared';

@Component({
  selector: 'app-manage-role',
  templateUrl: './manage-role.component.html',
  styleUrls: ['./manage-role.component.scss']
})
export class ManageRoleComponent implements OnInit {

  roleForm: FormGroup;
  isEdit: boolean;
  editId: string;
  oldRoleName: string;
  submitted: boolean;
  isAddFeature: boolean = true;
  isEditFeature: boolean = true;
  isAllowed: boolean = true;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly roleService: RoleManagementService,
    private readonly toasterService: ToasterService,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.initRoleInputForm();
    this.checkEditParam();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isAddFeature = res.featureJSON['canAdd'];
        this.isEditFeature = res.featureJSON['canEdit'];
        if (this.isAddFeature && this.isEditFeature) {
          this.isAllowed = true;
        } else if (this.isAddFeature && !this.isEdit) {
          this.isAllowed = true;
        } else if (!this.isEditFeature && this.isEdit) {
          this.isAllowed = false;
        } else if (!this.isAddFeature && this.isEdit) {
          this.isAllowed = true;
        } else if (this.isEditFeature && this.isEdit) {
          this.isAllowed = true;
        } else {
          this.isAllowed = false;
        }
      }
    });
  }

  checkEditParam() {
    const routeParams = this.route.snapshot.paramMap;
    this.editId = routeParams.get('id');
    if (this.editId) {
      this.isEdit = true;
      this.roleService.getRoleById(this.editId).subscribe(res => {
        if (res) {
          const obj = {
            name: res['name'],
            description: res['description']
          }
          this.oldRoleName = res['name'];
          this.roleForm.setValue(obj);
        }
      });
    }
  }

  initRoleInputForm() {
    this.roleForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      description: ['']
    });
  }

  get f() {
    return this.roleForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.roleForm.valid) {
      if (this.isEdit) {
        const data = {
          "id": this.editId,
          "name": this.roleForm.get('name').value,
          "oldRoleName": this.oldRoleName,
          "description": this.roleForm.get('description').value
        };
        this.roleService.updateRole(data).subscribe(() => {
          this.toasterService.showToast('success', 'Role updated successfully!', 'EMCARE');
          this.showRoles();
        });
      } else {
        const data = {
          "roleName": this.roleForm.get('name').value,
          "roleDescription": this.roleForm.get('description').value
        };
        this.roleService.createRole(data).subscribe((_res) => {
          this.toasterService.showToast('success', 'Role created successfully!', 'EMCARE');
          this.showRoles();
        });
      }
    }
  }

  showRoles() {
    this.router.navigate([`showRoles`]);
  }
}
