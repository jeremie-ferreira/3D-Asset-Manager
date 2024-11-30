import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ApiService } from '../services/api.service';
import { ModelViewerComponent } from '../components/model-viewer/model-viewer.component';
import { ActivatedRoute, Router } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatTabsModule } from '@angular/material/tabs';
import { MatStepperModule } from '@angular/material/stepper';
import { MatChipsModule } from '@angular/material/chips';
import {MatAutocompleteModule } from '@angular/material/autocomplete';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { TagSelectorComponent } from '../components/tag-selector/tag-selector.component';

@Component({
  selector: 'app-edit-asset',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatInputModule,
    MatIconModule,
    MatTabsModule,
    MatStepperModule,
    MatChipsModule,
    MatSelectModule,
    MatListModule,
    MatGridListModule,
    MatAutocompleteModule,
    FormsModule,
    MatFormFieldModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    ModelViewerComponent,
    TagSelectorComponent
  ],
  templateUrl: './edit-asset.component.html',
  styleUrl: './edit-asset.component.css',
})
export class EditAssetComponent implements OnInit  {
  @ViewChild(ModelViewerComponent) modelViewer!: ModelViewerComponent;
  @ViewChild(TagSelectorComponent) tagSelectorComponent!: TagSelectorComponent;
  selectedFile: File | null = null;
  asset: any = {};
  id: any;
  categories: any[] = [];
  isLoading = false;
  mainFormGroup: FormGroup;
  fileFormGroup: FormGroup;
  additionalFilesFormGroup: FormGroup;
  infoFormGroup: FormGroup;
  filteredTags: any[] = [];
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];

 
  constructor(private apiService: ApiService, private route: ActivatedRoute, private formBuilder: FormBuilder, private router: Router) {
    this.fileFormGroup = this.formBuilder.group({
      fileControl: ['', Validators.required],
      thumbnailControl: ['', Validators.required]
    });

    this.additionalFilesFormGroup = this.formBuilder.group({
      filesControl: [''],
    });

    this.infoFormGroup = this.formBuilder.group({
      nameControl: ['', Validators.required],
      descriptionControl: ['', Validators.required],
      categoryControl: [null, Validators.required],
      tagsControl: this.formBuilder.array([]),
      currentTagControl: ['']
    });

    this.mainFormGroup = this.formBuilder.group({
      fileFormGroup: this.fileFormGroup,
      additionalFilesFormGroup: this.additionalFilesFormGroup,
      infoFormGroup: this.infoFormGroup
    });
  }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.asset.additionalFiles = [];

    this.apiService.get<any[]>('categories').subscribe(
      categories => this.categories = categories
    );

    if (this.id !== 'new') {
      this.apiService.get('asset/' + this.id).subscribe(
        (asset: any) => {
          this.asset = asset;
          this.infoFormGroup.patchValue({
            nameControl: this.asset.name,
            descriptionControl: this.asset.description,
            categoryControl: this.asset.category
          });

          this.tagSelectorComponent.setTagsArrays(this.asset.tags);
          
          this.apiService.getBlob('file/' + this.asset.thumb.id).subscribe(
            (blob) => {
              this.asset.thumb.url = URL.createObjectURL(blob);
              this.fileFormGroup.get('thumbnailControl')?.setValue(this.asset.thumb);
            } 
          );

          this.apiService.getBlob("file/" + this.asset.mainFile.id).subscribe(
            (blob) => {
              this.asset.mainFile.url = URL.createObjectURL(blob);
              this.fileFormGroup.get('fileControl')?.setValue(this.asset.mainFile);
              this.modelViewer.initThree(this.asset.mainFile.url, {width: 400, height: 400});
            }
          );
        }
      );
    }
  }
  
  get tagsArray(): FormArray {
    return this.infoFormGroup.get('tagsControl') as FormArray;
  }

  get currentTagControl(): FormControl {
    return this.infoFormGroup.get('currentTagControl') as FormControl;
  }

  compareById(obj1: any, obj2: any): boolean {
    return obj1 && obj2 && obj1.id === obj2.id;
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const url = URL.createObjectURL(target.files[0]);
      
      const data = new FormData();
      data.append("file", target.files[0]);

      this.apiService.post('file', data).subscribe(
        (file) => {
          this.asset.mainFile = file;
          this.fileFormGroup.get('fileControl')?.setValue(file);
          this.modelViewer.initThree(url, {width: 400, height: 400});
        }
      );
    }
  }

  addAdditionalFile(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const data = new FormData();
      data.append("file", target.files[0]);
      this.apiService.post('file', data).subscribe(
        (file) => {
          this.asset.additionalFiles.push(file);
          console.log(this.asset.additionalFiles);
        }
      );
    }
  }

  thumbnailFromPreview() {
    const dataURL = this.modelViewer.captureImage(512, 512);
    const blob = this.apiService.dataURLToBlob(dataURL);
    const data = new FormData();
    data.append("file", blob, "thumb.png");

    this.apiService.post('file', data).subscribe(
      (file) => {
        this.asset.thumb = file;
        this.mainFormGroup.get('fileFormGroup.thumbnailControl')?.setValue(file);
      }
    );
  }

  onThumbFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      
      const data = new FormData();
      data.append("file", target.files[0]);
      this.apiService.post('file', data).subscribe(
        (file) => {
          this.asset.thumb = file;
          this.fileFormGroup.get('thumbnailControl')?.setValue(file);
        }
      );
    }
  }

  onSave() {
    this.markTouched('infoFormGroup');
    if (!this.infoFormGroup.valid) {
      alert('invalid');
      return;
    }

    this.asset.name = this.infoFormGroup.get('nameControl')?.value;
    this.asset.description = this.infoFormGroup.get('descriptionControl')?.value;
    this.asset.category = this.infoFormGroup.get('categoryControl')?.value;
    this.asset.tags = this.infoFormGroup.get('tagsControl')?.value;
    this.apiService.post('asset', this.asset).subscribe(
      (asset: any) => {
        this.asset = asset;
        this.router.navigate(['/asset', asset.id]);
      }
    );
  }

  removeFile(i: number) {
    this.asset.additionalFiles.splice(i);
  }

  markTouched(formGroupName: string): void {
    this.mainFormGroup.get(formGroupName)?.markAllAsTouched();
  }
}
