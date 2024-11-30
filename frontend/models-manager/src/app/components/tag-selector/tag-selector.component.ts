import { ChangeDetectorRef, Component, EventEmitter, forwardRef, Input, OnInit, Output } from '@angular/core';
import { ControlValueAccessor, FormArray, FormBuilder, FormControl, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule } from '@angular/forms';
import { MatChipInputEvent, MatChipsModule } from '@angular/material/chips';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { ApiService } from '../../services/api.service';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-tag-selector',
  standalone: true,
  templateUrl: './tag-selector.component.html',
  styles: `
  .tag-selector {
  width: 100%;
}

.chips-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap; /* Ensure chips wrap to the next line when space is limited */
}

.chips {
  display: flex;
  gap: 4px;
  flex-wrap: wrap; /* Allow chips to flow onto new lines if needed */
  max-width: calc(100% - 150px); /* Adjust based on available space */
}

.chips + input {
  flex-shrink: 0; /* Prevent the input from shrinking */
  min-width: 150px; /* Ensure the input remains a readable size */
}

mat-chip-row {
  margin: 2px; /* Add some spacing between chips */
}
  `,
  imports: [
    NgFor,
    MatChipsModule,
    MatAutocompleteModule,
    MatInputModule,
    MatIconModule,
    ReactiveFormsModule,
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TagSelectorComponent),
      multi: true,
    },
  ],
})
export class TagSelectorComponent implements OnInit, ControlValueAccessor {
  @Input() tagsArray!: FormArray;
  @Input() currentTagControl!: FormControl;
  @Input() allowCustomTags!: boolean;
  @Input() formGroup!: FormGroup;
  @Output() tagsChanged = new EventEmitter<string[]>();

  onChange: any;
  onTouched: any;

  isDisabled = false;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  filteredTags: any[] = [];

  constructor(private apiService: ApiService, private formBuilder: FormBuilder, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.currentTagControl.valueChanges.subscribe((value) => {
      this.filterTags(value.label || value);
    });
  }

  setTagsArrays(tags: []): void {
    this.tagsArray.clear();
    tags.forEach((tag: any) => {
      this.tagsArray.push(this.formBuilder.control(tag));
    });
  }

  writeValue(value: any): void {
    if (value !== undefined) {
      this.tagsArray.setValue(value);
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  add(event: MatChipInputEvent): void {
    if (!this.allowCustomTags) {
      return;
    }

    const input = event.chipInput.inputElement;
    const value = event.value?.trim();

    if (value && !this.tagsArray.value.includes(value)) {
      this.tagsArray.push(new FormControl({id: null, label: value}));
      this.tagsChanged.emit(this.tagsArray.value);
    }

    if (input) {
      input.value = '';
    }
    this.currentTagControl.setValue('');
  }

  remove(tag: string): void {
    const index = this.tagsArray.value.indexOf(tag);
    if (index >= 0) {
      this.tagsArray.removeAt(index);
      this.tagsChanged.emit(this.tagsArray.value);
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    const value = event.option.value;

    if (!this.tagsArray.value.some((tag: any) => tag.label === value.label)) {
      this.tagsArray.push(new FormControl(value));
      this.tagsChanged.emit(this.tagsArray.value);
    }

    this.currentTagControl.setValue('');
    this.forceDomUpdate('input[formControlName="currentTagControl"]', '');
  }

  forceDomUpdate(element: string, value: string) {
    const inputElement = document.querySelector(element);
    if (inputElement) {
      (inputElement as HTMLInputElement).value = value;
    }
  }

  filterTags(query: string): void {
    if (query.trim().length > 0) {
      this.apiService
        .get<any[]>(`tags/autocomplete?searchTerm=${query}`)
        .subscribe((tags) => {
          this.filteredTags = tags;
        });
    } else {
      this.filteredTags = [];
    }
  }
}
