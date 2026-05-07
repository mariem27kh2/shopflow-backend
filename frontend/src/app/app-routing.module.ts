import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ProductsComponent } from './products/products.component';
import { ShopComponent } from './shop/shop.component';
import { OrdersComponent } from './orders/orders.component';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { CartComponent } from './cart/cart.component';
import { CheckoutComponent } from './checkout/checkout.component';
import { CategoriesComponent } from './categories/categories.component';
import { AdminOrdersComponent } from './admin-orders/admin-orders.component';

import { authGuard } from './services/authGuard';
import { adminSellerGuard } from './services/adminSellerGuard';

import { SellerOrdersComponent } from './seller-orders/seller-orders.component';

import { ReviewsPendingComponent } from './reviews-pending/reviews-pending.component';

const routes: Routes = [
  { path: '', component: ShopComponent },
  { path: 'shop', component: ShopComponent },
  { path: 'product/:id', component: ProductDetailComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'cart', component: CartComponent, canActivate: [authGuard] },
  { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [authGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [adminSellerGuard] },
  { path: 'products', component: ProductsComponent, canActivate: [adminSellerGuard] },
  { path: 'categories', component: CategoriesComponent, canActivate: [adminSellerGuard] },
  { path: 'admin-orders', component: AdminOrdersComponent, canActivate: [adminSellerGuard] },
  { path: 'seller-orders', component: SellerOrdersComponent, canActivate: [adminSellerGuard] },
  { path: 'reviews-pending', component: ReviewsPendingComponent, canActivate: [adminSellerGuard] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
