import React from 'react';

const searches = [
    "The Tortured Poets Album",
    "New Releases",
    "Coachella",
    "Music Video",
    "Tyler The Creator"
];

const TopSearches = () => {
    return (
        <section className="topcomsearch-column">
            <div className="topsea">
                <h4 className="font3">TOP SEARCHES</h4>
                {searches.map((search, index) => (
                    <a href="#" className="topsearches-link" key={index}>
                        <h5 className="font">{index + 1}# {search}</h5>
                    </a>
                ))}
            </div>
        </section>
    );
};

export default TopSearches;
