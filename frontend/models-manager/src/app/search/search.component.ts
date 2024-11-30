import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatRippleModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../services/api.service';
import { HttpParams } from '@angular/common/http';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { SearchService } from '../services/search-service';
import { TagSelectorComponent } from '../components/tag-selector/tag-selector.component';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { combineLatest, concat, concatMap, forkJoin } from 'rxjs';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule,
    MatFormFieldModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatRippleModule,
    ScrollingModule,
    TagSelectorComponent
  ],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css'
})
export class SearchComponent implements OnInit {
  filters: any;
  categories: any[] = [];
  category: any;
  sortOptions = [{label: 'Ascending', value: 'asc'}, {label: 'Descending', value: 'desc'}];
  assets: any[] = [];
  searchTerm = ''; 
  page = 0;
  pageSize = 6;
  sort = 'name';
  order = this.sortOptions[0];
  hasMoreAssets = true;
  loading = false;
  searchFormGroup: FormGroup;
  initialized = false;

  constructor(private router: Router, private route: ActivatedRoute, private apiService: ApiService, private searchService: SearchService, private formBuilder: FormBuilder, private cdr: ChangeDetectorRef) {
    this.searchFormGroup = this.formBuilder.group({
      nameControl: [''],
      descriptionControl: [''],
      tagsControl: this.formBuilder.array([]),
      currentTagControl: ['']
    });
  }
  
  ngOnInit(): void {
    this.searchService.setSearchComponent(this);

    let paramsObservable = this.route.queryParams;
    let categoriesObservable = this.apiService.get<any[]>('categories');
    
    categoriesObservable.pipe(concatMap(categories => this.categories = [this.category, ...categories])).subscribe()
    combineLatest([categoriesObservable, paramsObservable]).subscribe(([categories, params]) => {
      //update categories
      this.categories = [{id: null, label: 'All Categories'}, ...categories];

      //update search from path params
      this.searchTerm = params['searchTerm'] || '';

      //update category from path params
      const pathCategory = params['category'];
      this.category = pathCategory == undefined ? this.categories[0] : this.categories.find(cat => cat.id == pathCategory);

      //update tags from path params
      const tagIds = params['tags'] ? params['tags'].split(',') : [];
      if (tagIds.length > 0) {
        const params = new HttpParams().appendAll({ tagIds });
        this.apiService.get<any[]>('tags', params).subscribe(tags => {
            this.tagsArray.clear();
            tags.forEach((tag: any) => {
              this.tagsArray.push(this.formBuilder.control(tag));
            });
            this.reloadSearch();
          }
        );
      } else {
        this.reloadSearch();
      }
    });
  }

  compareById(obj1: any, obj2: any): boolean {
    return obj1 && obj2 && obj1.id === obj2.id;
  }
  
  get tagsArray(): FormArray {
    return this.searchFormGroup.get('tagsControl') as FormArray;
  }

  get currentTagControl(): FormControl {
    return this.searchFormGroup.get('currentTagControl') as FormControl;
  }

  onTagsChanged(): void {
    this.reloadSearch();
  }
  
  onCategoryChange(event: any): void {
    this.category = event.value;
    this.reloadSearch();
  }
  
  onSortChange(event: any): void {
    this.order = event.value;
    this.reloadSearch();
  }

  search(query: any): void {
    this.searchTerm = query;
    this.reloadSearch();
  }

  reloadSearch(): void {
    this.page = 0;
    this.assets = [];
    this.hasMoreAssets = true;
    this.loadAssets();
  }

  loadPreview(asset: any): void {
    if (asset.thumb == null) return;

    this.apiService.getBlob('file/' + asset.thumb.id).subscribe(
      (blob) => {
        asset.thumb.url = URL.createObjectURL(blob);
      }
    );
  }

  loadAssets(): void {
    if (this.loading || !this.hasMoreAssets) return;

    const tagIds = this.tagsArray.value.map((tag: any) => tag.id);
    
    const params = new HttpParams()
      .set('search', this.searchTerm)
      .set('categoryId', this.category.id || '')
      .set('page', this.page.toString())
      .set('size', this.pageSize.toString())
      .set('sort', this.sort + ',' + this.order.value)
      .appendAll({ tagIds });

    this.loading = true;
    this.apiService.get<any[]>('assets/search', params).subscribe((page: any) => {
      this.assets = [...this.assets, ...page.content];

      for (const i in page.content) {
        this.loadPreview(page.content[i]);
      }

      this.hasMoreAssets = !page.last;
      this.page++;
      this.loading = false;
    });
  }

  onScroll(event: any) {
    const { scrollTop, scrollHeight, clientHeight } = event.target;
    
    if (scrollTop + clientHeight >= scrollHeight - 400) {
      this.loadAssets();
    }
  }

  goToDetails(id: number) {
    this.router.navigate(['/asset', id]);
  }
}
