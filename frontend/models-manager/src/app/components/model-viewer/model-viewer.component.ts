import { Component, ElementRef, ViewChild, HostListener, Input } from '@angular/core';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { RGBELoader } from 'three/examples/jsm/loaders/RGBELoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';

@Component({
  selector: 'app-model-viewer',
  standalone: true,
  imports: [],
  template: '<div #canvas></div>',
  styleUrl: './model-viewer.component.css'
})
export class ModelViewerComponent {
  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef;
  @Input() transparentBackground = false;
  @Input() autoRotation = false;

  private scene!: THREE.Scene;
  private camera!: THREE.PerspectiveCamera;
  private renderer!: THREE.WebGLRenderer;
  private controls!: OrbitControls;
  private model!: THREE.Object3D;
  private dimensions: any;

  initThree(fileUrl: string, dimensions: any): void {
    const width = dimensions.width;
    const height = dimensions.height;
    this.dimensions = dimensions;

    this.scene = new THREE.Scene();
    this.camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);

    // Initialize the renderer with alpha property for transparency
    this.renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    this.renderer.setSize(width, height);
    this.renderer.shadowMap.enabled = true;
    this.canvasRef.nativeElement.appendChild(this.renderer.domElement);
    this.onWindowResize();

    this.camera.position.set(0, 0, 1);
    this.camera.lookAt(0, 0, 0);

    // Initialize OrbitControls
    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.enableDamping = true;
    this.controls.dampingFactor = 0.25;
    this.controls.screenSpacePanning = true;

    // Load HDR environment map
    new RGBELoader()
      .setPath('assets/') // Replace with the path to your HDR file
      .load('studio_small_09_4k.hdr', (texture) => {
        texture.mapping = THREE.EquirectangularReflectionMapping;
        this.scene.environment = texture;
        //this.scene.background = texture;
        this.scene.background = this.transparentBackground ? null : new THREE.Color(0xf0f0f0);
        // Load the GLTF model with PBR materials
        const loader = new GLTFLoader();
        loader.load(fileUrl, (gltf) => {
          this.model = gltf.scene;
          this.model.traverse((node) => {
            if ((node as THREE.Mesh).isMesh) {
              node.castShadow = true;
            }
          });
          this.scene.add(this.model);
          this.adjustCamera(this.model);
          this.animate();
        }, undefined, (error) => {
          console.error('An error occurred while loading the model:', error);
        });
      });
  }

  adjustCamera(model: any) {
    // Calculate bounding box and adjust camera
    const box = new THREE.Box3().setFromObject(model);
    const boxSize = box.getSize(new THREE.Vector3());
    const boxCenter = box.getCenter(new THREE.Vector3());

    // Calculate distance for perspective camera to fit the model
    const maxDim = Math.max(boxSize.x, boxSize.y, boxSize.z);
    const fov = this.camera.fov * (Math.PI / 180); // Convert FOV to radians
    const cameraDistance = maxDim / (2 * Math.tan(fov / 2));

    // Set camera position and look at the model
    this.camera.position.copy(boxCenter.clone().add(new THREE.Vector3(0, 0, cameraDistance)));
    this.camera.lookAt(boxCenter);

    // Adjust near and far planes to avoid clipping
    this.camera.near = cameraDistance / 100;
    this.camera.far = cameraDistance * 10;
    this.camera.updateProjectionMatrix();

    // Set OrbitControls target to the model's center
    this.controls.target.copy(boxCenter);
    this.controls.update();
    
    // Create a shadow plane
    const largestSize = Math.max(boxSize.x, boxSize.y, boxSize.z) * 3;
    const shadowPlaneGeometry = new THREE.PlaneGeometry(largestSize, largestSize); // Adjust size as needed
    const shadowPlaneMaterial = new THREE.ShadowMaterial({ opacity: .3});
    const shadowPlane = new THREE.Mesh(shadowPlaneGeometry, shadowPlaneMaterial);

    // Rotate and position the shadow plane
    shadowPlane.rotation.x = -Math.PI / 2; // Make the plane horizontal
    shadowPlane.position.set(boxCenter.x, boxCenter.y - boxSize.y / 2 - 0.01, boxCenter.z); // Place just below the model
    shadowPlane.receiveShadow = true;
    this.scene.add(shadowPlane);

    // Add a directional light to cast shadows
    const light = new THREE.DirectionalLight(0xffffff, 1);
    light.position.set(largestSize, largestSize, largestSize); // Position the light
    light.castShadow = true;
    // Configure shadow properties for better visibility
    light.shadow.mapSize.width = 2048;
    light.shadow.mapSize.height = 2048;
    light.shadow.camera.left = -largestSize;
    light.shadow.camera.right = largestSize;
    light.shadow.camera.top = largestSize;
    light.shadow.camera.bottom = -largestSize;
    light.shadow.camera.near = .01 * largestSize;
    light.shadow.camera.far = 3 * largestSize;
    light.shadow.camera.updateProjectionMatrix();
    //this.scene.add(new THREE.CameraHelper(light.shadow.camera));
    this.scene.add(light);

    // Ensure the model casts shadows
    model.traverse((child: any) => {
      if (child.isMesh) {
        child.castShadow = true;
      }
    });
  }

  captureImage(width: number, height: number): string {
    this.updateCanvas(width, height);
    this.renderer.render(this.scene, this.camera);
    const dataURL = this.renderer.domElement.toDataURL('image/png');

    this.updateCanvas(this.dimensions.width, this.dimensions.height);

    return dataURL;
  }

  private updateCanvas(width: number, height: number): void {
    this.renderer.setSize(width, height);
    this.camera.aspect = width / height;
    this.camera.updateProjectionMatrix();
  }

  // Resize handler
  @HostListener('window:resize', ['$event'])
  onWindowResize(): void {
    const canvas = this.canvasRef.nativeElement;
    this.updateCanvas(canvas.clientWidth, canvas.clientHeight);
  }

  animate(): void {
    requestAnimationFrame(() => this.animate());

    if (this.autoRotation && this.model) {
      this.model.rotation.y += 0.001;  // Adjust this value to control the speed of rotation
    }

    this.controls.update();
    this.renderer.render(this.scene, this.camera);
  }
}
