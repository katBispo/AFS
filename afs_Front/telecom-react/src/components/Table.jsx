
export default function Table({columns, rows, keyField}){
  return (
    <div className="card">
      <table className="table">
        <thead>
          <tr>
            {columns.map(c=> <th key={c.key}>{c.header}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.map(row => (
            <tr key={row[keyField] ?? JSON.stringify(row)}>
              {columns.map(c => <td key={c.key}>{c.render? c.render(row): row[c.key]}</td>)}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
