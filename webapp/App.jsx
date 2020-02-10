class App extends React.Component {
  constructor(props) {
    super(props);
    
    this.state = {
      source: (
<div class="container">
  <div class="row">
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?shopping" title="Lista zakupów" description="Aplikacja Lista Zakupów" source="/produkty" replace={this.replace} back={this.back} />
    </div>
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?meeting" title="Kalendarz" description="Aplikacja Kalendarz" source="http://mojdzien.cba.pl/#app" replace={this.replace} back={this.back} />
    </div>
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?agenda" title="Launcher" description="Launcher dla Androida" source="http://mojdzien.cba.pl/#app" replace={this.replace} back={this.back} />
    </div>
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?rubbish" title="Puste" description="Puste" source="http://mojdzien.cba.pl/#app" replace={this.replace} back={this.back} />
    </div>
  </div>
</div>
      )
    }
  }
  
  replace = (source) => {
    this.setState({source: source});
  }
  
  back = () => {
    this.setState({source: <App />});
  }
  
  render() {
    return this.state.source
  }
}
