export const PDFStyles = {
    tableHeader: {
      fillColor: [79, 129, 189],
      textColor: [255, 255, 255],
      fontStyle: 'bold'
    },
    tableRow: {
      fillColor: [255, 255, 255],
      textColor: [0, 0, 0],
      fontStyle: 'normal'
    },
    alternateRow: {
      fillColor: [240, 240, 240],
      textColor: [0, 0, 0],
      fontStyle: 'normal'
    }
  };
  
  export function formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', { 
      style: 'currency', 
      currency: 'BRL' 
    }).format(value);
  }
  
  export function formatDate(date: Date): string {
    return new Intl.DateTimeFormat('pt-BR').format(date);
  }