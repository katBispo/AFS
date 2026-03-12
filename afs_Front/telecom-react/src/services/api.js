import axios from 'axios'
const api = axios.create({ baseURL: '' }) // usando proxy

// EXISTENTES
export const listarEquipamentos = () => api.get('/api/equipamentos').then(r=>r.data)
export const obterEquipamento   = (id) => api.get(`/api/equipamentos/${id}`).then(r=>r.data)
export const listarInterfaces   = (equipId) => api.get(`/api/interfaces?equipamentoId=${equipId}`).then(r=>r.data)
export const listarVlans        = (equipId) => api.get(`/api/vlans?equipamentoId=${equipId}`).then(r=>r.data)
export const listarRotas        = (equipId) => api.get(`/api/rotas?equipamentoId=${equipId}`).then(r=>r.data)
export const serieTemperatura   = (equipId, ini, fim) =>
  api.get(`/api/series/temperatura`, { params: { equipamentoId: equipId, ini, fim }}).then(r=>r.data)

// NOVOS
export const getEquipamentoByIp = (ip) => api.get(`/api/equipamentos/by-ip/${ip}`).then(r=>r.data)

export const listarPortasUltimas = (equipId) =>
  api.get(`/api/portas/ultimas/${equipId}`).then(r=>r.data)

export const estatPing = (equipId, ini, fim) =>
  api.get(`/api/protocolos/ping`, { params: { equipamentoId: equipId, ini, fim }}).then(r=>r.data)

export const estatSnmp = (equipId, versao, ini, fim) =>
  api.get(`/api/protocolos/snmp`, { params: { equipamentoId: equipId, versao, ini, fim }}).then(r=>r.data)

export const listarFontesUltimas = (equipId) =>
  api.get(`/api/fontes/ultimas/${equipId}`).then(r=>r.data)
export const listarRelesUltimas  = (equipId) =>
  api.get(`/api/reles/ultimas/${equipId}`).then(r=>r.data)

// ✅ Adicione esta função exatamente assim:
export const correlacaoIpVlan = (equipId) =>
  api.get(`/api/correlacoes/ip-vlan/${equipId}`).then(r => r.data)

// (Se você já tinha correlacaoRotasVsInterfaces, mantenha)
export const correlacaoRotasVsInterfaces = (equipId) =>
  api.get(`/api/correlacoes/rotas-vs-interfaces/${equipId}`).then(r => r.data)

export default api
