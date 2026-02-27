
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import dayjs from 'dayjs'
import { obterEquipamento, listarInterfaces, listarVlans, listarRotas, serieTemperatura, seriePing } from '../services/api'
import Table from '../components/Table'
import { TemperaturaChart, PingScatter } from '../components/Charts'

export default function EquipamentoDetalhe(){
  const { id } = useParams()
  const [equip, setEquip] = useState(null)
  const [ifaces, setIfaces] = useState([])
  const [vlans, setVlans] = useState([])
  const [rotas, setRotas] = useState([])
  const [temp, setTemp] = useState([])
  const [ping, setPing] = useState([])

  useEffect(()=>{
    obterEquipamento(id).then(setEquip)
    listarInterfaces(id).then(setIfaces)
    listarVlans(id).then(setVlans)
    listarRotas(id).then(setRotas)
    const fim = dayjs().toISOString()
    const ini = dayjs().subtract(2,'hour').toISOString()
    serieTemperatura(id, ini, fim).then(setTemp)
    seriePing(id, ini, fim).then(setPing)
  },[id])

  const colsIf = [
    {key:'porta', header:'Porta'},
    {key:'nome', header:'Nome'},
    {key:'tipo', header:'Tipo'},
    {key:'velocidadeMbps', header:'Velocidade (Mbps)'}
  ]
  const colsVlan = [ {key:'numero', header:'VLAN'} ]
  const colsRotas = [
    {key:'destino', header:'Destino'},
    {key:'prefixo', header:'Prefixo'},
    {key:'gateway', header:'Gateway'},
    {key:'tipo', header:'Tipo'}
  ]

  if(!equip) return <div>Carregando...</div>

  return (
    <div className="row">
      <div className="col">
        <h2>{equip.nome}</h2>
        <div className="card" style={{marginBottom:16}}>IP: {equip.ipPrincipal} · Modelo: {equip.modelo} · Versão: {equip.versaoSistema}</div>
        <h3>Interfaces</h3>
        <Table columns={colsIf} rows={ifaces} keyField="id" />
        <h3>VLANs</h3>
        <Table columns={colsVlan} rows={vlans} keyField="id" />
        <h3>Rotas</h3>
        <Table columns={colsRotas} rows={rotas} keyField="id" />
      </div>
      <div className="col">
        <TemperaturaChart data={temp} />
        <PingScatter data={ping} />
      </div>
    </div>
  )
}
