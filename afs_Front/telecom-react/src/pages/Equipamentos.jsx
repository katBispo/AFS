
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarEquipamentos } from '../services/api'

export default function Equipamentos(){
  const [data, setData] = useState([])
  useEffect(()=>{ listarEquipamentos().then(setData) },[])

  return (
    <div className="row">
      <div className="col">
        <h2>Equipamentos</h2>
        <div className="card">
          <ul>
            {data.map(e => (
              <li key={e.id}>
                <Link to={`/equipamentos/${e.id}`}>{e.nome} — {e.ipPrincipal}</Link>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  )
}
