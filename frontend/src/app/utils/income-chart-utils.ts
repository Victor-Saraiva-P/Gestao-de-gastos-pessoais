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
  
    // Converte os meses para um formato ordenável
    const monthMap: { [key: string]: number } = {
      janeiro: 1, fevereiro: 2, março: 3, abril: 4, maio: 5, junho: 6,
      julho: 7, agosto: 8, setembro: 9, outubro: 10, novembro: 11, dezembro: 12
    };
  
    const parsedData = Object.entries(barChartData.dadosMensais).map(([key, value]) => {
      const [monthName, year] = key.split(' ');
      return {
        month: monthMap[monthName.toLowerCase()],
        year: parseInt(year, 10),
        value
      };
    });
  
    // Ordena os dados cronologicamente
    const sortedData = parsedData.sort((a, b) => {
      if (a.year === b.year) {
        return a.month - b.month;
      }
      return a.year - b.year;
    });
  
    // Extrai os rótulos e valores ordenados
    const labels = sortedData.map(data => `${this.getMonthName(data.month)} ${data.year}`);
    const values = sortedData.map(data => data.value);
  
    // Cria o gráfico
    this.barChart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels,
        datasets: [{
          label: 'Receitas por Mês',
          data: values,
          backgroundColor: labels.map((_, index) => this.getColor(index)),
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
  
    if (this.barChart) {
      this.barChart.destroy();
    }
  
    // Agrupar receitas por mês/ano
    const totals: { [key: string]: number } = {};
    const dateMap: { [key: string]: Date } = {};
  
    filteredBarData.forEach(income => {
      const date = new Date(income.data);
      const year = date.getFullYear();
      const month = date.getMonth() + 1; // 1 a 12
      const key = `${year}-${String(month).padStart(2, '0')}`; // chave única no formato YYYY-MM
  
      totals[key] = (totals[key] || 0) + income.valor;
      dateMap[key] = new Date(year, month - 1); // salvar data real
    });
  
    // Ordenar pelas datas reais
    const sortedKeys = Object.keys(totals).sort((a, b) => {
      return dateMap[a].getTime() - dateMap[b].getTime();
    });
  
    const months = sortedKeys.map(key => {
      const [year, month] = key.split('-');
      return `${this.getMonthName(Number(month))} ${year}`; // Exibe o nome do mês e o ano
    });
    const values = sortedKeys.map(key => totals[key]);
  
    // Criar gráfico
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
          title: {
            display: true,
            text: 'Receitas por Mês'
          }
        }
      }
    });
  }
  
  private static getMonthName(month: number): string {
    const monthNames = [
      'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
      'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
    ];
    return monthNames[month - 1];
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
