export interface Income {
    uuid?: string;
    data: Date;
    categoria: string;
    valor: number;
    origemDoPagamento: string;
    observacoes: string;
}