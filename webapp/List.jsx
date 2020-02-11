class List extends React.Component {
  constructor(props) {
    super(props);
    
    this.state = {
      list: this.props.list,
      back: this.props.back,
      selected: null
    }
  }
  
  handleClick = () => {
    let self = this;
    axios.get("/produkt?nazwa=" + this.state.selected).then(function (response) {
      self.props.replace(<List properties={["sklep", "cena", "dodano"]} list={response.data} replace={self.props.replace} back={self.back} />);
    })
  }
  render() {
    return (
<div class="container">
  <div class="row mt-3">
    <a href="#app" class="btn btn-primary" onClick={this.props.back}>{localise.powrot}</a>
    <a class="btn p-0" href='https://play.google.com/store/apps/details?id=com.gmail.lesniakwojciech.listazakupowa&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img height="36px" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>
  </div>
  <div class="row mt-3">
    <table class="table table-hover">
      <thead class="thead-dark">
        <tr>
          {this.props.properties.map(property => {
            return (<th>{String(localise[property]).toUpperCase()}</th>)
          })}
          {this.props.expandable && <th>{localise.wiecej.toUpperCase()}</th>}
        </tr>
      </thead>
      <tbody>
      {this.props.list.map(row => {
        return (<tr onMouseOver={() => this.setState({ selected: row[this.props.properties[0]] })}>
          {this.props.properties.map(property => {
            return <td>{row[property]}</td>
          })}
          {this.props.expandable && <td><span class="badge badge-secondary" onClick={this.handleClick}>...</span></td>}
          </tr>)
      })}
      </tbody>
    </table>
  </div>
</div>
    );
  }
  
  back = () => {
    this.props.replace(<List properties={["produkt", "sklep", "cena", "dodano"]} list={this.state.list} expandable={true} replace={this.props.replace} back={this.state.back} />);
  }
}
