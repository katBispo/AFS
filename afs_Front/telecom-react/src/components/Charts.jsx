
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, ScatterChart, Scatter, Legend } from 'recharts'

export function TemperaturaChart({data}){
  return (
    <div className="card">
      <h3>Temperatura (°C) vs. Tempo</h3>
      <ResponsiveContainer width="100%" height={280}>
        <LineChart data={data}>
          <CartesianGrid stroke="#1f2640" />
          <XAxis dataKey="timestamp" tick={{fill:'#a6b0c3'}}/>
          <YAxis tick={{fill:'#a6b0c3'}}/>
          <Tooltip labelStyle={{color:'#000'}}/>
          <Line type="monotone" dataKey="valorCelsius" stroke="#4ea1ff" dot={false} />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}

export function PingScatter({data}){
  return (
    <div className="card">
      <h3>Ping: Latência (ms) × Perda (%)</h3>
      <ResponsiveContainer width="100%" height={280}>
        <ScatterChart>
          <CartesianGrid stroke="#1f2640" />
          <XAxis dataKey="rttMs" name="rttMs" tick={{fill:'#a6b0c3'}} label={{ value:'RTT (ms)', position:'insideBottom', offset:-5, fill:'#a6b0c3' }}/>
          <YAxis dataKey="perdaPercentual" name="perda" tick={{fill:'#a6b0c3'}} label={{ value:'Perda (%)', angle:-90, position:'insideLeft', fill:'#a6b0c3' }}/>
          <Tooltip cursor={{strokeDasharray:'3 3'}}/>
          <Legend />
          <Scatter name="Amostras" data={data} fill="#82ca9d" />
        </ScatterChart>
      </ResponsiveContainer>
    </div>
  )
}
