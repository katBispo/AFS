
import { useEffect, useState } from 'react'
import { listarEquipamentos } from '../services/api'
import Table from '../components/Table'
import { StatusBadge } from '../components/StatusBadge'

export default function Dashboard() {
  const [equipamentos, setEquipamentos] = useState([])
  useEffect(() => { listarEquipamentos().then(setEquipamentos).catch(() => setEquipamentos([])) }, [])
  const cols = [
    { key: 'ipPrincipal', header: 'IP' },
    { key: 'nome', header: 'Nome', render: r => r.nome || '—' },
    { key: 'modelo', header: 'Modelo' },
    { key: 'status', header: 'Status', render: (r) => <StatusBadge status={r.status} /> },
  ]
  return (
    <div className="row">
      <div className="col">
        <h2>Equipamentos</h2>
        <Table columns={cols} rows={equipamentos} keyField="id" />
      </div>
    </div>
  )
}
