import { Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CustomCategoryService {

  private apiUrl = environment.apiUrl + '/categorias';
}
