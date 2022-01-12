import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FeatureManagementService } from 'src/app/root/services/feature-management.service';
@Component({
  selector: 'app-feature-list',
  templateUrl: './feature-list.component.html',
  styleUrls: ['./feature-list.component.scss']
})
export class FeatureListComponent implements OnInit {

  mainFeatureList: any;
  filteredFeatureList: any;
  searchString: string;
  isAPIBusy: boolean = true;

  constructor(
    private readonly router: Router,
    private readonly featureService: FeatureManagementService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.getAllFeatures();
  }

  getAllFeatures() {
    this.mainFeatureList = [];
    this.featureService.getAllFeatures().subscribe(res => {
      if (res) {
        this.mainFeatureList = res;
        this.filteredFeatureList = this.mainFeatureList;
        this.isAPIBusy = false;
      }
    });
  }

  searchFilter() {
    this.filteredFeatureList = this.mainFeatureList.filter(feature => {
      return feature.menuName?.toLowerCase().includes(this.searchString);
    })
  }

  updateFeature(index) {
    this.router.navigate([`editFeature/${this.filteredFeatureList[index]['id']}`],
      {queryParams: { name: this.filteredFeatureList[index]['menuName']}});
  }
}
