import { LayoutModule } from '@angular/cdk/layout';
import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink } from '@angular/router';
import { ModelViewerComponent } from "../components/model-viewer/model-viewer.component";

@Component({
  selector: 'app-storefront',
  standalone: true,
  imports: [MatToolbarModule, MatFormFieldModule, MatButtonModule, MatInputModule, LayoutModule, RouterLink, ModelViewerComponent],
  templateUrl: './storefront.component.html',
  styleUrl: './storefront.component.css'
})
export class StorefrontComponent implements AfterViewInit {
  @ViewChild(ModelViewerComponent) modelViewer!: ModelViewerComponent;

  ngAfterViewInit(): void {
    console.log(this.modelViewer);
    this.modelViewer.initThree('/assets/WoodenSphere.glb', {width: 400, height: 400});
  }
}
