import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  searchComponent: any;

  setSearchComponent(component: any) {
    this.searchComponent = component;
  }

  updateSearch(query: string) {
    this.searchComponent.search(query);
  }
}
