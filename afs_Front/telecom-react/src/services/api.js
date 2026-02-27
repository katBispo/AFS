
import axios from 'axios'

const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080' })

export const listarEquipamentos = () => api.get('/api/equipamentos').then(r=>r.data)
export const obterEquipamento = (id) => api.get(`/api/equipamentos/${id}`).then(r=>r.data)
export const listarInterfaces = (equipId) => api.get(`/api/interfaces?equipamentoId=${equipId}`).then(r=>r.data)
export const listarVlans = (equipId) => api.get(`/api/vlans?equipamentoId=${equipId}`).then(r=>r.data)
export const listarRotas = (equipId) => api.get(`/api/rotas?equipamentoId=${equipId}`).then(r=>r.data)
export const correlacaoRotasVsInterfaces = (equipId) => api.get(`/api/correlacoes/rotas-vs-interfaces/${equipId}`).then(r=>r.data)
export const correlacaoIpVlan = (equipId) => api.get(`/api/correlacoes/ip-vlan/${equipId}`).then(r=>r.data)
export const serieTemperatura = (equipId, ini, fim) => api.get(`/api/series/temperatura`, { params: { equipamentoId: equipId, ini, fim }}).then(r=>r.data)
export const seriePing = (equipId, ini, fim) => api.get(`/api/series/ping`, { params: { equipamentoId: equipId, ini, fim }}).then(r=>r.data)

export default api
