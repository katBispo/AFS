
import { Routes, Route, NavLink, Navigate } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import Equipamentos from './pages/Equipamentos'
import EquipamentoDetalhe from './pages/EquipamentoDetalhe'
import Correlacoes from './pages/Correlacoes'

export default function App(){
  return (
    <>
      <header className="header">
        <div style={{fontWeight:700}}>📡 Telecom Monitoramento</div>
        <nav className="nav">
          <NavLink to="/" end>Dashboard</NavLink>
          <NavLink to="/equipamentos">Equipamentos</NavLink>
          <NavLink to="/correlacoes">Correlações</NavLink>
        </nav>
      </header>
      <div className="container">
        <Routes>
          <Route path="/" element={<Dashboard/>} />
          <Route path="/equipamentos" element={<Equipamentos/>} />
          <Route path="/equipamentos/:id" element={<EquipamentoDetalhe/>} />
          <Route path="/correlacoes" element={<Correlacoes/>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </div>
    </>
  )
}
