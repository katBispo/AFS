
export function StatusBadge({status}){
  const s = (status||'').toLowerCase()
  const cls = s==='ok'?'ok': s==='alerta'?'alerta': s==='critico'?'critico':''
  return <span className={`badge ${cls}`}>{status}</span>
}
