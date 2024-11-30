import { Routes } from '@angular/router';
import { StorefrontComponent } from './storefront/storefront.component';
import { SearchComponent } from './search/search.component';
import { AssetComponent } from './asset/asset.component';
import { EditAssetComponent } from './edit-asset/edit-asset.component';
import { LoginComponent } from './login/login.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { AuthGuard } from './services/auth-guard';

export const routes: Routes = [
    { path: '', component: StorefrontComponent },
    { path: 'login', component: LoginComponent },
    { path: 'search', component: SearchComponent, canActivate: [AuthGuard]  },
    { path: 'asset/:id', component: AssetComponent, canActivate: [AuthGuard]  },
    { path: 'asset/:id/edit', component: EditAssetComponent, canActivate: [AuthGuard]  },
    { path: 'edit/profile', component: EditProfileComponent, canActivate: [AuthGuard]  },
    { path: '**', redirectTo: '/login' }
];
