export const environment = {
  production: true,
  apiUrl: (window as any)["env"]?.apiUrl || 'https://backend-gestao-gastos.onrender.com/auth'
};