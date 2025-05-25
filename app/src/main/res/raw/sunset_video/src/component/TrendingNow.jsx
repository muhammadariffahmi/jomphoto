import React from 'react';

const TrendingNow = () => {
    return (
        <section className="trendingcontainer">
            <h3 className="trendingfont">TRENDING NOW</h3>
            <div className="slider-wrapper">
                <div className="slider">
                    <img id="slide1" src="albums/album1.jpg" alt="Album 1" />
                    <img id="slide2" src="albums/album2.jpeg" alt="Album 2" />
                    <img id="slide3" src="albums/album3.jpg" alt="Album 3" />
                    <img id="slide4" src="albums/album4.jpg" alt="Album 4" />
                    <img id="slide5" src="albums/album5.jpeg" alt="Album 5" />
                </div>
                <div className="slider-nav">
                    <a href="#slide1"></a>
                    <a href="#slide2"></a>
                    <a href="#slide3"></a>
                    <a href="#slide4"></a>
                    <a href="#slide5"></a>
                </div>
            </div>
        </section>
    );
};

export default TrendingNow;
