import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { Income } from '../../entity/income';

@Injectable({
  providedIn: 'root'
})
export class IncomePdfService {

  async generateIncomeReport(
    incomes: Income[],
    chartData?: {
      pieChart: any | null,
      barChart: any | null
    },
    filters?: { 
      startDate?: string, 
      endDate?: string,
      minValue?: number | null,
      maxValue?: number | null
    }
  ): Promise<void> {
    const pdf = new jsPDF('p', 'mm', 'a4');
    
    // 1. Cabeçalho
    this.addHeader(pdf, filters);
    
    // 2. Gráficos (se disponíveis)
    let currentY = 40;
    
    if (chartData?.pieChart) {
      currentY = await this.addPieChart(pdf, chartData.pieChart, currentY);
    }
    
    if (chartData?.barChart) {
      currentY = await this.addBarChart(pdf, chartData.barChart, currentY);
    }
    
    // 3. Tabela de receitas
    this.addIncomesTable(pdf, incomes, currentY);
    
    // 4. Rodapé
    this.addFooter(pdf);
    
    // 5. Salvar
    pdf.save(this.generateFileName(filters));
  }

  private addHeader(pdf: jsPDF, filters?: any): void {
    pdf.setFontSize(20);
    pdf.setTextColor(40);
    pdf.text('Relatório de Receitas', 105, 20, { align: 'center' });
    
    if (filters?.startDate || filters?.endDate) {
      pdf.setFontSize(12);
      
      const start = filters.startDate ? new Date(filters.startDate).toLocaleDateString() : 'Início';
      const end = filters.endDate ? new Date(filters.endDate).toLocaleDateString() : 'Atual';
      pdf.text(`Período: ${start} à ${end}`, 105, 30, { align: 'center' });
    }

    if (filters?.minValue || filters?.maxValue) {
      pdf.setFontSize(12);
      const min = filters.minValue ? `R$ ${filters.minValue.toFixed(2)}` : 'Mínimo';
      const max = filters.maxValue ? `R$ ${filters.maxValue.toFixed(2)}` : 'Máximo';
      pdf.text(`Filtro de valores: ${min} - ${max}`, 105, 40, { align: 'center' });
    }
    
    pdf.setDrawColor(200);
    pdf.setLineWidth(0.5);
    pdf.line(20, 50, 190, 50);
  }

  private async addPieChart(pdf: jsPDF, chartData: any, yPosition: number): Promise<number> {
    // Verifica espaço na página
    if (yPosition > pdf.internal.pageSize.height - 40) {
        pdf.addPage();
        yPosition = 20;
    }

    pdf.setFontSize(14);
    pdf.text('', 10, yPosition);
    yPosition += 30;

    // Verifica se há dados para exibir
    if (!chartData || !chartData.categorias || Object.keys(chartData.categorias).length === 0) {
        pdf.setFontSize(10);
        pdf.text('Nenhuma categoria com dados no período selecionado', 20, yPosition);
        return yPosition + 20;
    }

    const canvas = document.createElement('canvas');
    canvas.width = 600;
    canvas.height = 600;
    const ctx = canvas.getContext('2d');
    
    if (!ctx) return yPosition;

    console.log(canvas.toDataURL('image/png'));

    // Filtra categorias com valores > 0
    const categories = Object.entries(chartData.categorias)
        .filter(([_, value]) => Number(value) > 0)
        .sort((a, b) => Number(b[1]) - Number(a[1])); // Ordena por valor decrescente

    if (categories.length === 0) {
        pdf.setFontSize(10);
        pdf.text('Nenhuma categoria com dados no período selecionado', 20, yPosition);
        return yPosition + 20;
    }

    // Cores para as categorias
    const colors = [
        '#4BC0C0', '#36A2EB', '#FFCE56', '#FF6384', '#9966FF',
        '#FF9F40', '#8AC24A', '#F06292', '#7986CB', '#A1887F'
    ];

    // Calcula o total apenas das categorias filtradas
    const total = categories.reduce((sum, [_, value]) => sum + Number(value), 0);
    const centerX = 200;
    const centerY = 200;
    const radius = 150;
    let startAngle = 0;

    // Desenha apenas as fatias com dados
    categories.forEach(([name, value], index) => {
        const sliceAngle = (Number(value) / total) * 2 * Math.PI;
        
        ctx.fillStyle = colors[index % colors.length];
        ctx.beginPath();
        ctx.moveTo(centerX, centerY);
        ctx.arc(centerX, centerY, radius, startAngle, startAngle + sliceAngle);
        ctx.closePath();
        ctx.fill();

        // Desenha a legenda
        const legendX = 350;
        const legendY = 50 + (index * 20);
        ctx.fillStyle = colors[index % colors.length];
        ctx.fillRect(legendX, legendY - 10, 15, 15);
        ctx.fillStyle = '#000';
        ctx.font = '10px Arial';
        
        // Formata o texto: "Categoria (XX%) - R$ YYY.YY"
        const percentage = ((Number(value) / total) * 100).toFixed(1);
        const formattedValue = Number(value).toFixed(2);
        ctx.fillText(`${name} (${percentage}%) - R$ ${formattedValue}`, legendX + 20, legendY);

        startAngle += sliceAngle;
    });

    const imgData = canvas.toDataURL('image/png');
    const imgWidth = 170;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;

    // Verifica espaço para a imagem
    if (yPosition + imgHeight > pdf.internal.pageSize.height - 20) {
        pdf.addPage();
        yPosition = 20;
    }

    pdf.addImage(imgData, 'PNG', 20, yPosition, imgWidth, imgHeight);
    return yPosition + imgHeight + 20;
  }

  private async addBarChart(pdf: jsPDF, chartData: any, yPosition: number): Promise<number> {
    if (yPosition > 250) {
        pdf.addPage();
        yPosition = 20;
    }
    
    pdf.setFontSize(14);
    pdf.text('Receitas Mensais', 10, yPosition);
    yPosition += 15;

    // Verificação dos dados
    if (!chartData?.dadosMensais || Object.keys(chartData.dadosMensais).length === 0) {
        pdf.setFontSize(10);
        pdf.text('Nenhum dado disponível para o período selecionado', 20, yPosition);
        return yPosition + 20;
    }

    // Filtra e ordena os meses corretamente
    const filteredMonths = Object.entries(chartData.dadosMensais)
        .filter(([_, value]) => Number(value) > 0)
        .sort((a, b) => a[0].localeCompare(b[0]));

    if (filteredMonths.length === 0) {
        pdf.setFontSize(10);
        pdf.text('Nenhum dado disponível para o período selecionado', 20, yPosition);
        return yPosition + 20;
    }

    // Criação do canvas
    const canvas = document.createElement('canvas');
    const monthsCount = filteredMonths.length;
    canvas.width = Math.min(1000, Math.max(600, monthsCount * 80));
    canvas.height = 400;
    const ctx = canvas.getContext('2d');
    
    if (!ctx) return yPosition;

    // Configurações do gráfico
    const margin = { top: 40, right: 30, bottom: 60, left: 60 };
    const chartWidth = canvas.width - margin.left - margin.right;
    const chartHeight = canvas.height - margin.top - margin.bottom;
    const barColor = '#36A2EB';
    const maxValue = Math.max(...filteredMonths.map(([_, value]) => Number(value)));
    const barWidth = Math.min(60, chartWidth / filteredMonths.length);

    // Desenha eixos
    ctx.beginPath();
    ctx.moveTo(margin.left, margin.top);
    ctx.lineTo(margin.left, margin.top + chartHeight);
    ctx.lineTo(margin.left + chartWidth, margin.top + chartHeight);
    ctx.strokeStyle = '#000';
    ctx.lineWidth = 1;
    ctx.stroke();

    // Adiciona rótulos do eixo Y
    ctx.font = '10px Arial';
    ctx.textAlign = 'right';
    ctx.fillStyle = '#000';
    const ySteps = 5;
    for (let i = 0; i <= ySteps; i++) {
        const value = (maxValue / ySteps) * i;
        const y = margin.top + chartHeight - (i/ySteps * chartHeight);
        ctx.fillText(`R$ ${value.toFixed(2)}`, margin.left - 5, y + 4);
    }

    // Desenha barras e rótulos
    filteredMonths.forEach(([month, value], index) => {
        const numericValue = Number(value);
        const barHeight = (numericValue / maxValue) * chartHeight;
        const x = margin.left + (index * barWidth);
        const y = margin.top + chartHeight - barHeight;
        
        // Barra
        ctx.fillStyle = barColor;
        ctx.fillRect(x, y, barWidth - 5, barHeight);
        
        // Valor no topo da barra
        ctx.font = '10px Arial';
        ctx.textAlign = 'center';
        ctx.fillStyle = '#000';
        ctx.fillText(`R$ ${numericValue.toFixed(2)}`, x + (barWidth / 2), y - 5);
        
        // Rótulo do mês
        const monthLabel = this.formatMonthLabel(month);
        ctx.save();
        ctx.translate(x + (barWidth / 2), margin.top + chartHeight + 20);
        ctx.rotate(-Math.PI/4);
        ctx.textAlign = 'right';
        ctx.fillText(monthLabel, 0, 0);
        ctx.restore();
    });

    // Adiciona a imagem ao PDF
    const imgData = canvas.toDataURL('image/png');
    const imgWidth = 180;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;
    
    if (yPosition + imgHeight > pdf.internal.pageSize.height - 20) {
        pdf.addPage();
        yPosition = 20;
    }

    pdf.addImage(imgData, 'PNG', 20, yPosition, imgWidth, imgHeight);
    return yPosition + imgHeight + 20;
  }

  private addIncomesTable(pdf: jsPDF, incomes: Income[], startY: number, filters?: { 
    startDate?: string, 
    endDate?: string,
    minValue?: number | null,
    maxValue?: number | null
  }): void {
    // Filtra as receitas pelos critérios selecionados
    const filteredIncomes = incomes.filter(income => {
      // Filtro por data
      if (filters?.startDate || filters?.endDate) {
        if (!income.data) return false;
        
        const incomeDate = new Date(income.data);
        const startDate = filters?.startDate ? new Date(filters.startDate) : null;
        const endDate = filters?.endDate ? new Date(filters.endDate) : null;
        
        if (endDate) endDate.setHours(23, 59, 59, 999);
        
        const dateValid = (!startDate || incomeDate >= startDate) && 
                         (!endDate || incomeDate <= endDate);
        if (!dateValid) return false;
      }

      // Filtro por valor
      if (filters?.minValue || filters?.maxValue) {
        const value = income.valor || 0;
        const minValid = !filters?.minValue || value >= filters.minValue;
        const maxValid = !filters?.maxValue || value <= filters.maxValue;
        
        if (!minValid || !maxValid) return false;
      }

      return true;
    });

    // Se não houver dados filtrados, exibe mensagem
    if (filteredIncomes.length === 0) {
      pdf.setFontSize(10);
      
      let message = 'Nenhuma receita encontrada';
      if (filters?.startDate || filters?.endDate) {
        message += ' no período selecionado';
      }
      if (filters?.minValue || filters?.maxValue) {
        message += ' com os valores filtrados';
      }
      
      pdf.text(message, 20, startY);
      return;
    }

    // Prepara os dados para a tabela
    const tableData = filteredIncomes.map(income => [
      income.data ? new Date(income.data).toLocaleDateString() : '-',
      income.categoria || '-',
      `R$ ${income.valor?.toFixed(2) || '0,00'}`,
      income.origemDoPagamento || '-',
      income.observacoes?.substring(0, 30) + (income.observacoes?.length > 30 ? '...' : '') || '-'
    ]);
    
    // Adiciona a tabela ao PDF
    autoTable(pdf, {
      startY: startY,
      head: [['Data', 'Categoria', 'Valor', 'Origem', 'Observações']],
      body: tableData,
      margin: { left: 20, right: 20 },
      styles: { 
        fontSize: 8, 
        cellPadding: 3,
        overflow: 'linebreak',
        minCellHeight: 8
      },
      headStyles: { 
        fillColor: [44, 62, 80],
        textColor: 255,
        fontStyle: 'bold'
      },
      alternateRowStyles: {
        fillColor: [240, 240, 240]
      },
      columnStyles: {
        0: { cellWidth: 'auto' },
        1: { cellWidth: 'auto' },
        2: { cellWidth: 'auto', halign: 'right' },
        3: { cellWidth: 'auto' },
        4: { cellWidth: 'wrap' }
      }
    });
  }

  private addFooter(pdf: jsPDF): void {
    pdf.setFontSize(10);
    pdf.setTextColor(150);
    pdf.text(
        `Gerado em ${new Date().toLocaleDateString()}`,
        20,
        pdf.internal.pageSize.height - 10
    );
  }

  private generateFileName(filters?: any): string {
    let name = 'relatorio-receitas';
    if (filters?.startDate) {
      name += `_${new Date(filters.startDate).toISOString().split('T')[0]}`;
    }
    if (filters?.endDate) {
      name += `-${new Date(filters.endDate).toISOString().split('T')[0]}`;
    }
    return `${name}.pdf`;
  }

  private formatMonthLabel(monthString: string): string {
    if (!monthString) return '';
    
    try {
        const [year, month] = monthString.split('-');
        if (!year || !month) return monthString;
        
        const monthNames = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 
                          'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];
        const monthIndex = parseInt(month) - 1;
        if (monthIndex < 0 || monthIndex > 11) return monthString;
        
        return `${monthNames[monthIndex]}/${year.substring(2)}`;
    } catch {
        return monthString;
    }
  }
}