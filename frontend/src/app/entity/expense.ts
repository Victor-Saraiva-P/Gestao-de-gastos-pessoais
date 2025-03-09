export interface Expense {
    uuid?: string;
    data: Date;
    categoria: string;
    valor: number;
    destinoPagamento: string;
    observacoes: string;
}