class App extends React.Component {
  constructor(props) {
    super(props);
    
    this.state = {
      ekran: (
<div class="container">
  <div class="row">
    <div class="col-sm">
      <Card ikona="https://source.unsplash.com/400x225/?shopping" tytul="Lista zakupów" zrodlo="/lista.php" opis="Aplikacja Lista Zakupów" aktualizuj={this.aktualizuj} powroc={this.powroc} />
    </div>
    <div class="col-sm">
      <Card ikona="https://source.unsplash.com/400x225/?meeting" tytul="Kalendarz" opis="Aplikacja Kalendarz" aktualizuj={this.aktualizuj} />
    </div>
    <div class="col-sm">
      <Card ikona="https://source.unsplash.com/400x225/?finding" tytul="DuckDuckGo" opis="Wyszukiwarka" zrodlo="https://duckduckgo.com" />
    </div>
    <div class="col-sm">
      <Card ikona="https://source.unsplash.com/400x225/?map" tytul="OpenStreetMap" opis="Otwarte mapy" zrodlo="https://www.openstreetmap.org" />
    </div>
  </div>
</div>
      )
    }
  }
  
  aktualizuj = (ekran) => {
    this.setState({ekran: ekran});
  }
  
  powroc = () => {
    this.setState({ekran: <App />});
  }
  
  render() {
    return this.state.ekran
  }
}