function ShortUrlOutputPanel(props) {
    return <div className='flex-row bg-white flex-fill'>
        <div className='flex-column'>
               <a href={props.url}>{props.url}</a>
        </div>
    </div>
}

export default ShortUrlOutputPanel;