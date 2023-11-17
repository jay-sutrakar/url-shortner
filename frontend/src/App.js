import { useState } from 'react'
import ShortUrlOutputPanel from './ShortUrlOutputPanel';

function App() {
  
  const blackborder = {
     border: '2px solid black'
    }

  const [inputUrl , setInputUrl ] = useState('');
  const [response , setResponse ] = useState('');

  const handleInputUrlValue = (event) => {
      setInputUrl(event.target.value)
  }
  const handleSubmit = (event) => {
    createShortUrl();
    setResponse(inputUrl)
    event.preventDefault();
  }

  const createShortUrl = () => {
    const url = 'http://localhost:8080/api/v1/data/shorturl?url=' + inputUrl;
    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then(data => {
        setResponse(data)
      })
      .catch(error => {
        console.log(error);
      })
  }

  let outputPanel;
  if (response !== '') {
    outputPanel = <ShortUrlOutputPanel url={response.short_url}/>
  }
  return (
    <div className="container" style={blackborder}>
      
      <form onSubmit={handleSubmit}>
      <div className='row row-cols-2'>
        <div className='col-10'>
          <input type='text' className='form-control col' onChange={handleInputUrlValue} placeholder='Enter your url' id='inputUrl'/>
        </div>
        <div className='col-2'>
          <button type='submit' className='btn btn-primary'>Generate</button>
        </div>
      </div>
      </form>
      <div className="d-flex bg-info justify-content-start align-items-center">
        {outputPanel}
      </div>
    </div>
  );
}

export default App;
