class App extends React.Component {
  constructor(props) {
    super(props);
    
    this.state = {
      source: (
<div class="container">
  <div class="row">
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?shopping" title={localise.listaZakupow} description={localise.aplikacjaListaZakupow} source="/produkty" replace={this.replace} back={this.back} />
    </div>
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?meeting" title={localise.kalendarz} description={localise.aplikacjaKalendarz} source="http://mojdzien.cba.pl/#app" replace={this.replace} back={this.back} />
    </div>
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?agenda" title={localise.launcher} description={localise.launcherDlaAndroida} source="http://mojdzien.cba.pl/#app" replace={this.replace} back={this.back} />
    </div>
    <div class="col-sm">
      <Card image="https://source.unsplash.com/400x225/?rubbish" title={localise.puste} description={localise.puste} source="http://mojdzien.cba.pl/#app" replace={this.replace} back={this.back} />
    </div>
  </div>
  <div class="row"><a href='https://play.google.com/store/apps/developer?id=Wojciech+Le%C5%9Bniak&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' height="100px" src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a></div>
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
