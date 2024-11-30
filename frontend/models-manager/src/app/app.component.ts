import { Component, HostListener, OnInit } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { AuthService } from './services/auth.service';
import { MatIconModule } from '@angular/material/icon';
import { MatRippleModule } from '@angular/material/core';
import { MatMenuModule } from '@angular/material/menu';
import { FormControl, ReactiveFormsModule  } from '@angular/forms';
import { SearchService } from './services/search-service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatMenuModule,
    MatRippleModule,
    ReactiveFormsModule,
    RouterModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'models-manager';
  isHomeRoute = false;
  isLargeScreen = true;
  isSearchRoute = false;
  pictureUrl = "";
  searchControl = new FormControl();
  searchResults = [];
  showNav = true;
  constructor(public authService: AuthService, private router: Router, private searchService: SearchService) {}

  ngOnInit(): void {
    this.pictureUrl = localStorage.getItem("profilePicture") || "/assets/pp.jpg";

    this.authService.isAuthenticated();
    
    this.router.events.subscribe(() => {
      this.isSearchRoute = this.router.url.startsWith('/search');
      this.isLargeScreen = window.innerWidth >= 768;
      this.showNav = !(['', '/', 'login', '/login'].includes(this.router.url));
    });
  }

  onSearch(searchTerm: string) {    
    this.searchService.updateSearch(searchTerm);
  }

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.isLargeScreen = window.innerWidth >= 768;
  }

  onViewProfile() {
    // Navigate to profile page
  }

  onSettings() {
    // Open settings page
  }

  onLogout() {
    this.authService.logout();
  }
}
