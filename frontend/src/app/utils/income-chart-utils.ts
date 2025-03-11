import { Income } from '../entity/income';
import { Expense } from '../entity/expense';
import { Chart, ChartConfiguration, registerables } from 'chart.js';

// Registrar todos os componentes do Chart.js
Chart.register(...registerables);

export class ChartUtils {
  private static pieChart: Chart | null = null;
  private static barChart: Chart | null = null;

  // Cores para os gráficos
  static getColor(index: number): string {
    const colors = [
      "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF",
      "#FF9F40", "#D4AF37", "#8A2BE2", "#20B2AA", "#DC143C",
      "#FFD700", "#4682B4", "#32CD32", "#FF4500", "#6A5ACD",
      "#008080", "#8B0000", "#556B2F", "#D2691E", "#1E90FF"
    ];
    return colors[index % colors.length];
  }

  // Destruir gráficos ao sair da página
  static destroyCharts(): void {
    if (this.pieChart) {
      this.pieChart.destroy();
      this.pieChart = null;
    }
    if (this.barChart) {
      this.barChart.destroy();
      this.barChart = null;
    }
  }

  // Desenha o gráfico de pizza usando Chart.js
  static drawPieChart(slices: { label: string, value: number }[], canvasId: string): void {
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
    if (!canvas || slices.length === 0) return;

    // Destruir gráfico existente se houver
    if (this.pieChart) {
      this.pieChart.destroy();
    }

    // Calcular o total para as porcentagens
    const totalValue = slices.reduce((sum, slice) => sum + slice.value, 0);

    // Pré-processar os dados para evitar distorções com poucos valores
    const labels = slices.map(slice => {
      const percentage = ((slice.value / totalValue) * 100).toFixed(1);
      return `${slice.label}: ${percentage}%`;
    });
    const data = slices.map(slice => slice.value);
    const backgroundColor = slices.map((_, index) => this.getColor(index));

    this.pieChart = new Chart(canvas, {
      type: 'pie', // Alterado de 'doughnut' para 'pie'
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: backgroundColor,
          borderWidth: 1,
          borderColor: '#ffffff',
          hoverOffset: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: true,
        aspectRatio: 2.3,
        plugins: {
          legend: {
            position: 'right',
            labels: {
              boxWidth: 10,
              font: {
                size: 11
              }
            }
          },
          title: {
            display: true,
            text: 'Distribuição de Receitas',
            font: {
              size: 14
            }
          },
          tooltip: {
            callbacks: {
              label: function(context) {
                const value = context.raw as number;
                const formattedValue = new Intl.NumberFormat('pt-BR', {
                  style: 'currency',
                  currency: 'BRL'
                }).format(value);
                return formattedValue;
              }
            }
          }
        },
        layout: {
          padding: 10
        }
        // Removido o 'cutout' que criava o efeito de rosca
      }
    });

    // Ajustar o tamanho do canvas para tamanho menor
    canvas.style.height = '220px';
  }

  // Desenha o gráfico de barras com dados do backend
  static drawBarChartFromBackend(
    barChartData: { dadosMensais: { [key: string]: number } } | null,
    canvasId: string
  ): void {
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
    if (!canvas || !barChartData) return;

    // Destruir gráfico existente se houver
    if (this.barChart) {
      this.barChart.destroy();
    }

    const months = Object.keys(barChartData.dadosMensais);
    const values = months.map(m => barChartData.dadosMensais[m]);

    this.barChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: months,
        datasets: [{
          label: 'Receitas por Mês',
          data: values,
          backgroundColor: months.map((_, index) => this.getColor(index)),
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            title: {
              display: true,
              text: 'Valor (R$)'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Mês'
            }
          }
        },
        plugins: {
          legend: {
            display: false
          },
          title: {
            display: true,
            text: 'Receitas por Mês'
          }
        }
      }
    });
  }

  // Gera o gráfico de barras a partir das receitas filtradas
  static generateBarChart(filteredBarData: Income[], canvasId: string): void {
    const canvas = document.getElementById(canvasId) as HTMLCanvasElement;
    if (!canvas) return;

    // Destruir gráfico existente se houver
    if (this.barChart) {
      this.barChart.destroy();
    }

    // Agrega receitas por mês
    const totals: { [key: string]: number } = {};
    filteredBarData.forEach(income => {
      const date = new Date(income.data);
      const monthYear = `${date.toLocaleString('default', { month: 'short' })} ${date.getFullYear()}`;
      totals[monthYear] = (totals[monthYear] || 0) + income.valor;
    });

    const months = Object.keys(totals).sort((a, b) => {
      const [monthA, yearA] = a.split(' ');
      const [monthB, yearB] = b.split(' ');
      const dateA = new Date(`${yearA}-${this.getMonthNumber(monthA)}-01`);
      const dateB = new Date(`${yearB}-${this.getMonthNumber(monthB)}-01`);
      return dateA.getTime() - dateB.getTime();
    });

    const values = months.map(m => totals[m]);

    this.barChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: months,
        datasets: [{
          label: 'Receitas',
          data: values,
          backgroundColor: months.map((_, index) => this.getColor(index)),
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true
          }
        },
        plugins: {
          title: {
            display: true,
            text: 'Receitas por Mês'
          }
        }
      }
    });
  }

  // Métodos auxiliares
  private static getMonthNumber(month: string): number {
    const monthsMap: { [key: string]: number } = {
      'jan': 1, 'fev': 2, 'mar': 3, 'abr': 4, 'mai': 5, 'jun': 6,
      'jul': 7, 'ago': 8, 'set': 9, 'out': 10, 'nov': 11, 'dez': 12,
      'jan.': 1, 'fev.': 2, 'mar.': 3, 'abr.': 4, 'mai.': 5, 'jun.': 6,
      'jul.': 7, 'ago.': 8, 'set.': 9, 'out.': 10, 'nov.': 11, 'dez.': 12
    };
    return monthsMap[month.toLowerCase()] || 0;
  }

  // Prepara dados para o gráfico de pizza
  static getPieChartSlicesFromBackend(
    pieChartData: { categorias: { [key: string]: number } } | null
  ): { label: string, value: number }[] {
    if (!pieChartData) return [];
    return Object.entries(pieChartData.categorias).map(([categoria, valor]) => ({ label: categoria, value: valor }));
  }

  // Prepara dados de receitas para o gráfico de pizza
  static getPieChartSlicesFromIncomes(incomes: Income[]): { label: string, value: number }[] {
    // Agrupar por categoria
    const categories: { [key: string]: number } = {};
    incomes.forEach(income => {
      categories[income.categoria] = (categories[income.categoria] || 0) + income.valor;
    });

    return Object.entries(categories).map(([categoria, valor]) => ({ label: categoria, value: valor }));
  }

  // Prepara dados de despesas para o gráfico de pizza
  static getPieChartSlicesFromExpenses(expenses: Expense[]): { label: string, value: number }[] {
    // Agrupar por categoria
    const categories: { [key: string]: number } = {};
    expenses.forEach(expense => {
      categories[expense.categoria] = (categories[expense.categoria] || 0) + expense.valor;
    });

    return Object.entries(categories).map(([categoria, valor]) => ({ label: categoria, value: valor }));
  }

  // Converter dados de backend para formato de legendas
  static getPieChartLegend(
    pieChartData: { categorias: { [key: string]: number } } | null
  ): Income[] {
    if (!pieChartData) return [];
    return Object.entries(pieChartData.categorias).map(([categoria, valor]) => ({
      categoria,
      valor,
      data: new Date(),
      origemDoPagamento: '',
      observacoes: ''
    } as Income));
  }
}
