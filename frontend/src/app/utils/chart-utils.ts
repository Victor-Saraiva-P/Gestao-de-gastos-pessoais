import { Income } from '../entity/income';

export class ChartUtils {
  // Retorna o elemento SVG pelo ID
  static getSvgElement(id: string): SVGSVGElement | null {
    const element = document.getElementById(id);
    return element instanceof SVGSVGElement ? element : null;
  }

  // Desenha um slice (fatia) do gráfico de pizza
  static drawPieSlice(
    svg: SVGSVGElement,
    centerX: number,
    centerY: number,
    radius: number,
    startAngle: number,
    sliceAngle: number,
    color: string
  ): void {
    const endAngle = startAngle + sliceAngle;
    const x1 = centerX + radius * Math.cos(startAngle);
    const y1 = centerY + radius * Math.sin(startAngle);
    const x2 = centerX + radius * Math.cos(endAngle);
    const y2 = centerY + radius * Math.sin(endAngle);
    const largeArcFlag = sliceAngle > Math.PI ? 1 : 0;
    const pathData = `M ${centerX} ${centerY} L ${x1} ${y1} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${x2} ${y2} Z`;

    const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    path.setAttribute('d', pathData);
    path.setAttribute('fill', color);
    svg.appendChild(path);
  }

  // Retorna uma cor com base no índice
  static getColor(index: number): string {
    const colors = [
      "#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF",
      "#FF9F40", "#D4AF37", "#8A2BE2", "#20B2AA", "#DC143C",
      "#FFD700", "#4682B4", "#32CD32", "#FF4500", "#6A5ACD",
      "#008080", "#8B0000", "#556B2F", "#D2691E", "#1E90FF"
    ];
    return colors[index % colors.length];
  }

  // Desenha o gráfico de pizza a partir dos slices fornecidos
  static drawPieChart(slices: { label: string, value: number }[], svgId: string): void {
    const svg = this.getSvgElement(svgId);
    if (!svg || slices.length === 0) return;
    svg.innerHTML = '';

    // Se só houver um slice, desenha um círculo completo
    if (slices.length === 1) {
      const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
      circle.setAttribute('cx', '150');
      circle.setAttribute('cy', '150');
      circle.setAttribute('r', '100');
      circle.setAttribute('fill', this.getColor(0));
      svg.appendChild(circle);
      return;
    }

    // Caso contrário, segue o desenho normal dos slices
    const total = slices.reduce((sum, slice) => sum + slice.value, 0);
    let startAngle = 0;
    const radius = 100;
    const centerX = 150, centerY = 150;

    slices.forEach((slice, index) => {
      const sliceAngle = (slice.value / total) * 2 * Math.PI;
      this.drawPieSlice(svg, centerX, centerY, radius, startAngle, sliceAngle, this.getColor(index));
      startAngle += sliceAngle;
    });
  }


  // Desenha o gráfico de barras com dados vindos do backend
  static drawBarChartFromBackend(
    barChartData: { dadosMensais: { [key: string]: number } } | null,
    svgId: string
  ): void {
    const svg = this.getSvgElement(svgId);
    if (!svg || !barChartData) return;
    svg.innerHTML = '';

    const months = Object.keys(barChartData.dadosMensais);
    if (months.length === 0) return;

    const values = months.map(m => barChartData.dadosMensais[m]);
    const maxValor = Math.max(...values);
    const barWidth = 50;
    const barSpacing = 100;
    const startX = 50;
    const startY = 350;
    const chartHeight = 200;

    months.forEach((month, index) => {
      const barHeight = (barChartData.dadosMensais[month] / maxValor) * chartHeight;
      const x = startX + index * barSpacing;
      const y = startY - barHeight;

      const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttribute("x", x.toString());
      rect.setAttribute("y", y.toString());
      rect.setAttribute("width", barWidth.toString());
      rect.setAttribute("height", barHeight.toString());
      rect.setAttribute("fill", this.getColor(index));
      svg.appendChild(rect);

      // Rótulo do mês (exibe as 3 primeiras letras)
      const displayMonth = month.split(' ')[0].substring(0, 3);
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", (x + barWidth / 2).toString());
      text.setAttribute("y", (startY + 30).toString());
      text.setAttribute("text-anchor", "middle");
      text.setAttribute("font-size", "20px");
      text.textContent = displayMonth;
      svg.appendChild(text);

      // Valor acima da barra
      const valueText = document.createElementNS("http://www.w3.org/2000/svg", "text");
      valueText.setAttribute("x", (x + barWidth / 2).toString());
      valueText.setAttribute("y", (y - 10).toString());
      valueText.setAttribute("text-anchor", "middle");
      valueText.setAttribute("font-size", "18");
      valueText.textContent = `R$${barChartData.dadosMensais[month].toFixed(0)}`;
      svg.appendChild(valueText);
    });

    // Linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

  // Gera o gráfico de barras a partir das receitas filtradas (fallback)
  static generateBarChart(filteredBarData: Income[], svgId: string): void {
    const svg = this.getSvgElement(svgId);
    if (!svg) return;
    svg.innerHTML = '';

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
    const maxValor = Math.max(...values);
    const barWidth = 25;
    const barSpacing = 50;
    const startX = 50;
    const startY = 350;
    const chartHeight = 200;

    months.forEach((month, index) => {
      const barHeight = (totals[month] / maxValor) * chartHeight;
      const x = startX + index * barSpacing;
      const y = startY - barHeight;

      const rect = document.createElementNS("http://www.w3.org/2000/svg", "rect");
      rect.setAttribute("x", x.toString());
      rect.setAttribute("y", y.toString());
      rect.setAttribute("width", barWidth.toString());
      rect.setAttribute("height", barHeight.toString());
      rect.setAttribute("fill", this.getColor(index));
      svg.appendChild(rect);

      // Rótulo do mês
      const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
      text.setAttribute("x", (x + barWidth / 2).toString());
      text.setAttribute("y", (startY + 15).toString());
      text.setAttribute("text-anchor", "middle");
      text.setAttribute("font-size", "12px");
      text.textContent = month;
      svg.appendChild(text);
    });

    // Linha do eixo X
    const xAxis = document.createElementNS("http://www.w3.org/2000/svg", "line");
    xAxis.setAttribute("x1", "40");
    xAxis.setAttribute("y1", startY.toString());
    xAxis.setAttribute("x2", (months.length * barSpacing + 50).toString());
    xAxis.setAttribute("y2", startY.toString());
    xAxis.setAttribute("stroke", "black");
    xAxis.setAttribute("stroke-width", "2");
    svg.appendChild(xAxis);
  }

  // Converte abreviação do mês para número (auxiliar para ordenação)
  private static getMonthNumber(month: string): number {
    const monthsMap: { [key: string]: number } = {
      'jan.': 1, 'fev.': 2, 'mar.': 3, 'abr.': 4, 'mai.': 5, 'jun.': 6,
      'jul.': 7, 'ago.': 8, 'set.': 9, 'out.': 10, 'nov.': 11, 'dez.': 12
    };
    return monthsMap[month.toLowerCase()] || 0;
  }

  // Gera os slices do gráfico de pizza a partir dos dados do backend
  static getPieChartSlicesFromBackend(
    pieChartData: { categorias: { [key: string]: number } } | null
  ): { label: string, value: number }[] {
    if (!pieChartData) return [];
    return Object.entries(pieChartData.categorias).map(([categoria, valor]) => ({ label: categoria, value: valor }));
  }

  // Gera os slices do gráfico de pizza a partir das receitas filtradas
  static getPieChartSlicesFromIncomes(incomes: Income[]): { label: string, value: number }[] {
    return incomes.map(income => ({ label: income.categoria, value: income.valor }));
  }

  // Retorna a legenda do gráfico de pizza (converte os dados do backend para um array de Income)
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
