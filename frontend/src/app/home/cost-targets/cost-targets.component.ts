import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { Categoria } from '../../entity/categoria';
import { CustomCategoryService } from '../custom-category/custom-category.service';
import { Router } from '@angular/router';
import { Target } from '../../entity/costTarget';
import { CostTargetService } from './cost-target.service';

@Component({
  selector: 'app-cost-targets',
  imports: [CommonModule , FormsModule, ReactiveFormsModule],
  templateUrl: './cost-targets.component.html',
  styleUrl: './cost-targets.component.css'
})
export class CostTargetsComponent implements OnInit{
  
  expensesTarget: Target[] = [];
  expenseCategories: Categoria[] = [];
  isRemoving = false;
  isEditing = false;
  modalType: 'create' | 'edit' | null = null;
  editingTarget: Target | null = null;
  editingTargetId: string | null = null;

  private router = inject(Router);
  private costTargetService = inject(CostTargetService);
  private customCategoryService = inject(CustomCategoryService);
  private fb = inject(FormBuilder);

  createTargetExpenseForm: FormGroup = this.fb.group({
    categoria: ['', Validators.required],
    valorLimite: ['', Validators.required],
    periodo: ['', Validators.required],
  });

  editTargetExpenseForm: FormGroup = this.fb.group({
    categoria: ['', Validators.required],
    valorLimite: ['', Validators.required],
    periodo: ['', Validators.required],
  });

  async ngOnInit() {
    await this.loadTarget();
    await this.loadCategories();
  }

  async loadTarget() {
    const response = await this.costTargetService.getAllTargets();
    if (response) {
      this.expensesTarget = response.sort((a, b) => a.categoria.localeCompare(b.categoria));
    }
  }

  async loadCategories() {
    const response = await this.customCategoryService.getAllExpenseCategories();
    if (response) {
      this.expenseCategories = response.filter(categoria => 
        categoria.nome !== 'Sem Categoria'
      ).sort((a, b) => a.nome.localeCompare(b.nome));
    }
  }

  toggleRemoveMode() {
    this.isRemoving = !this.isRemoving;
    if (this.isEditing) {
      this.isRemoving = false;
    }
  }

  toggleEditMode() {
    this.isEditing = !this.isEditing;
    if (this.isRemoving) {
      this.isEditing = false;
    }
  }

  openModal(type: 'create' | 'edit') {
    this.modalType = type;
  }

  closeModal() {
    this.modalType = null;
  }

  voltar() {
    this.router.navigate(['/home/expense']);
  }

  refreshPage() {
    window.location.reload();
  }


  onSubmitCreate() {
      if (this.createTargetExpenseForm.valid) {
        const { categoria, valorLimite, periodo } = this.createTargetExpenseForm.value;
        const newTarget: Target = {
          categoria: this.correctCategory(categoria),
          valorLimite,
          periodo
        };
  
        this.costTargetService
          .createTarget(newTarget)
          .then(() => {
            alert('Meta criada com sucesso!');
            this.refreshPage();
          })
          .catch((err) => alert('Erro ao criar meta: ' + err));
      }
  }

  async onSubmitRemove(id: string) {
    try {
      await this.costTargetService.deleteTarget(id);
      alert('Meta removida com sucesso!');
      await this.loadTarget();
      this.refreshPage();
    } catch (err) {
      alert('Erro ao remover meta: ' + err);
    }
  }

  async onSubmitEdit(id: string) {
    if (this.editTargetExpenseForm.valid) {
      try {
        const { categoria, valorLimite, periodo }: Target = this.editTargetExpenseForm.value;
        const newTarget: Target = {
          categoria: this.correctCategory(categoria),
          valorLimite,
          periodo
        };
        await this.costTargetService.uptadeTarget(id, newTarget);
        alert('Meta atualizada com sucesso!');
        this.refreshPage();
      } catch (err) {
        alert('Erro ao atualizar Meta: ' + err);
      }
    }
  }

  openEditModal(target: Target) {
    this.modalType = 'edit';
    this.editingTargetId = target.uuid!;
    this.editTargetExpenseForm.setValue({
      categoria: target.categoria,
      valorLimite: target.valorLimite,
      periodo: target.periodo
    });
}

  correctCategory(string: string): string {
    const newString = string.toLowerCase();
    return newString.charAt(0).toUpperCase() + newString.slice(1);
  }

}
