import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CategorizedComponents } from 'src/app/shared/edge/edgeconfig';
import { EdgeConfig, Service, Utils } from '../../../../shared/shared';

interface MyCategorizedComponents extends CategorizedComponents {
  isNatureClicked?: Boolean,
  filteredComponents?: EdgeConfig.Component[]
}

@Component({
  selector: IndexComponent.SELECTOR,
  templateUrl: './index.component.html',
})
export class IndexComponent implements OnInit {

  private static readonly SELECTOR = "indexComponentUpdate";

  public config: EdgeConfig = null;
  public list: MyCategorizedComponents[];

  public showAllEntries = false;

  constructor(
    private route: ActivatedRoute,
    private service: Service,
  ) {
  }

  public ngOnInit() {
    this.service.setCurrentComponent({ languageKey: 'Edge.Config.Index.adjustComponents' }, this.route);
    this.service.getConfig().then(config => {
      this.config = config;
      let categorizedComponentIds: string[] = [];
      this.list = config.listActiveComponents(categorizedComponentIds);
      for (let entry of this.list) {
        entry.isNatureClicked = false;
        entry.filteredComponents = entry.components;
      }
      this.updateFilter("");
    });
  }

  updateFilter(completeFilter: string) {
    // take each space-separated string as an individual and-combined filter
    let filters = completeFilter.toLowerCase().split(' ');
    let countFilteredEntries = 0;
    for (let entry of this.list) {
      entry.filteredComponents = entry.components.filter(entry =>
        // Search for filter strings in Component-ID, -Alias and Factory-ID
        Utils.matchAll(filters, [
          entry.id.toLowerCase(),
          entry.alias.toLowerCase(),
          entry.factoryId,
        ]),
      );
      countFilteredEntries += entry.filteredComponents.length;
    }
    // If not more than 5 Entries survived filtering -> show all of them immediately
    if (countFilteredEntries > 5) {
      this.showAllEntries = false;
    } else {
      this.showAllEntries = true;
    }
  }
}
