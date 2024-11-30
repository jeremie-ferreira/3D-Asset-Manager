import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ApiService } from '../services/api.service';
import { ModelViewerComponent } from "../components/model-viewer/model-viewer.component";

@Component({
  selector: 'app-asset',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    MatButtonModule,
    MatListModule,
    MatIconModule,
    MatProgressSpinnerModule,
    ModelViewerComponent,
    ModelViewerComponent
],
  templateUrl: './asset.component.html',
  styleUrl: './asset.component.css'
})
export class AssetComponent implements OnInit {
  details = {
    id: 1,
    name: "Transformateur 1500V Schneider",
    tags: ['3D', 'CAD', 'Architectural', 'Sniper', 'Action', 'Lowpoly', 'Stock', 'Bolt', 'Suppressor', 'Weapon'],
    files: [
      {name: "Transformateur.glb", type: "GLB", url: "#"}, {name: "Transformateur.spp", type: "Substance", url: "#"}
    ],
    compatibility: 'Blender, Maya, Unity',
    views: 1234,
    downloads: 567,
    rating: 4.5,
    author: {
      name: "John Doe",
      avatar: "/assets/pp.jpg"
    }
  };
  @ViewChild(ModelViewerComponent) modelViewer!: ModelViewerComponent;
  asset: any;
  modelUrl: any;
  constructor(private apiService: ApiService, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.apiService.get('asset/' + this.route.snapshot.paramMap.get('id')).subscribe(
      (asset) => {
        this.asset = asset;
        this.loadModel();
        this.apiService.getBlob('file/' + this.asset.author.profilePicture.id).subscribe(
          (blob) => this.asset.author.profilePicture.url = URL.createObjectURL(blob)
        );
      }
    );
  }

  loadModel(): void {
    this.apiService.getBlob("file/" + this.asset.mainFile.id).subscribe(
      (blob) => {
        this.modelUrl = URL.createObjectURL(blob);
        this.modelViewer.initThree(this.modelUrl, {width: 600, height: 400});
      }
    );
  }

  downloadFile(file: any): void {
    this.apiService.getBlob("file/" + file.id).subscribe(
      (blob) => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = file.name;
        link.click();
      }
    );
  }
}
