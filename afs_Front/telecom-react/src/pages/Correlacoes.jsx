
import { useEffect, useState } from 'react'
import { correlacaoRotasVsInterfaces, correlacaoIpVlan, listarEquipamentos } from '../services/api'

export default function Correlacoes(){
  const [equipamentos, setEquipamentos] = useState([])
  const [equipId, setEquipId] = useState('')
  const [avisos, setAvisos] = useState([])
  const [ipVlan, setIpVlan] = useState([])

  useEffect(()=>{ listarEquipamentos().then(d=>{ setEquipamentos(d); if(d[0]) setEquipId(String(d[0].id)) }) },[])
  useEffect(()=>{
    if(!equipId) return
    correlacaoRotasVsInterfaces(equipId).then(setAvisos)
    correlacaoIpVlan(equipId).then(setIpVlan)
  },[equipId])

  return (
    <div className="row">
      <div className="col">
        <h2>Correlações</h2>
        <div className="card" style={{marginBottom:16}}>
          <label>Equipamento: </label>
          <select value={equipId} onChange={e=>setEquipId(e.target.value)}>
            {equipamentos.map(e => <option key={e.id} value={e.id}>{e.nome}</option>)}
          </select>
        </div>
        <div className="card">
          <h3>Rotas × Interfaces</h3>
          <ul>
            {avisos.map((a,i)=> <li key={i}>{a}</li>)}
          </ul>
        </div>
        <div className="card" style={{marginTop:16}}>
          <h3>IP ↔ VLAN</h3>
          <table className="table">
            <thead>
              <tr><th>IP</th><th>Prefixo</th><th>VLAN</th><th>Porta</th></tr>
            </thead>
            <tbody>
              {ipVlan.map((r,i)=> (
                <tr key={i}><td>{r.ip}</td><td>{r.prefixo}</td><td>{r.vlan ?? '-'}</td><td>{r.porta ?? '-'}</td></tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}
