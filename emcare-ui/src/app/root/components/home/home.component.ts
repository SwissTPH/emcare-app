import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import * as Highcharts from 'highcharts';
import { AuthGuard } from 'src/app/auth/auth.guard';
import { FhirService } from 'src/app/shared';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  dashboardData: any = {};
  isView = true;

  @ViewChild('mapRef', { static: true }) mapElement: ElementRef;

  constructor(
    private readonly fhirService: FhirService,
    private routeService: Router,
    private readonly authGuard: AuthGuard
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.checkFeatures();
    this.fhirService.getDashboardData().subscribe((res) => {
      this.dashboardData = res;
    });
    this.barChartPopulation();
    this.pieChartBrowser();
    this.loadMap();
  }

  checkFeatures() {
    this.authGuard.getFeatureData().subscribe(res => {
      if (res.relatedFeature && res.relatedFeature.length > 0) {
        this.isView = res.featureJSON['canView'];
      }
    });
  }

  barChartPopulation() {
    Highcharts.chart('barChart', {
      chart: {
        type: 'bar'
      },
      title: {
        text: 'Patients per country'
      },
      xAxis: {
        categories: ['Africa', 'America', 'Asia', 'Europe', 'Oceania'],
      },
      yAxis: {
        min: 0,
        title: {
          text: 'Patients (millions)',
          align: 'high'
        },
      },
      tooltip: {
        valueSuffix: ' millions'
      },
      plotOptions: {
        bar: {
          dataLabels: {
            enabled: true
          }
        }
      },
      series: [{
        type: undefined,
        name: 'Year 1800',
        data: [107, 31, 635, 203, 2]
      }, {
        type: undefined,
        name: 'Year 1900',
        data: [133, 156, 947, 408, 6]
      }, {
        type: undefined,
        name: 'Year 2000',
        data: [814, 841, 3714, 727, 31]
      }, {
        type: undefined,
        name: 'Year 2016',
        data: [1216, 1001, 4436, 738, 40]
      }]
    });
  }

  pieChartBrowser() {
    Highcharts.chart('pieChart', {
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: 'Patients in October, 2021'
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enabled: true,
            format: '<b>{point.name}</b>: {point.percentage:.1f} %'
          }
        }
      },
      series: [{
        name: 'Countries',
        colorByPoint: true,
        type: undefined,
        data: [{
          name: 'Africa',
          y: 61.41,
          sliced: true,
          selected: true
        }, {
          name: 'America',
          y: 11.84
        }, {
          name: 'UAE',
          y: 10.85
        }, {
          name: 'Europe',
          y: 4.67
        }, {
          name: 'India',
          y: 4.18
        }, {
          name: 'China',
          y: 1.64
        }, {
          name: 'Australia',
          y: 1.6
        }, {
          name: 'Russia',
          y: 1.2
        }, {
          name: 'Iraq',
          y: 2.61
        }]
      }]
    });
  }

  loadMap = () => {
    const positions = { lat: 33.2232, lng: 43.6793 };
    const map = new window['google'].maps.Map(this.mapElement.nativeElement, {
      center: positions, zoom: 7
    });

    const marker = new window['google'].maps.Marker({
      position: positions,
      map: map,
      title: 'Map!',
      draggable: true,
      animation: window['google'].maps.Animation.DROP,
    });

    const contentString = '<div id="content">' +
      '<div id="siteNotice">' +
      '</div>' +
      '<h3 id="thirdHeading" class="thirdHeading">Iraq</h3>' +
      '<div id="bodyContent">' +
      '<p>Welcome to Iraq</p>' +
      '</div>' +
      '</div>';

    const infowindow = new window['google'].maps.InfoWindow({
      content: contentString
    });

    marker.addListener('click', function () {
      infowindow.open(map, marker);
    });
  }

  redirectToRoute(route: string) {
    this.routeService.navigate([route]);
  }
}