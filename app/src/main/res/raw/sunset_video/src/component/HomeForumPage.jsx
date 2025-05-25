// HomeForumPage.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../styles/HomeForumStyle.css';

const HomeForumPage = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);

    const openModal = () => setIsModalOpen(true);
    const closeModal = () => setIsModalOpen(false);

    return (
        <div>
            <main className="homewrapper">
                <section className="topdis-topcomsea-group">
                    <section>
                        <h3 className="font2">RECOMMENDED FOR YOU</h3>
                        <div id="postOutput" className="newpost">
                            {/* Display created posts here */}
                        </div>
                        <div className="discussion">
                            <Link to="/comment" className="discussion-link">
                                <div className="discussion-box">
                                    <h3 className="font1">Taylor Swift - The Tortured Poets Department ALBUM REVIEW</h3>
                                    <p className="font1">Reviews: Rolling Stone: 100/100 The Tortured Poets Department combines the intimacy of Folklore and Evermore with the synth-pop gloss of Midnights to create music that's wildly ambitious and gloriously chaotic. The Independent: 5/5 With its playful narratives and hooks like anchors, Swift’s 11th studio album is a terrific reminder of her storytelling powers. Variety: 94/100 Taylor Swift Renews Her Vows With Heartbreak in Audacious, Transfixing ‘Tortured Poets Department’ The Irish Times: 4.5/5 This album is the fruit of abject misery but is also steeped in Swift’s trademark indefatigable optimism.</p>
                                </div>
                            </Link>
                        </div>
                        <div className="discussion">
                            <Link to="/comment" className="discussion-link">
                                <div className="discussion-box">
                                    <h3 className="font1">What songs sound like they’d fit perfect for a movie soundtrack, even though they aren’t?</h3>
                                    <p className="font1">Are there any songs throughout time, that you thought would sound really good for a specific movie (or TV series) despite not being associated with the film in any way? I thought “My Boy Only Breaks His Favorite Toys” by Taylor Swift would’ve been great for the Barbie movie, she even references Ken. It would’ve been a fitting song for when Ken has his tantrum! “II MOST WANTED” by Beyoncé & Miley would fit well in any sisterhood type movie, maybe Little Women? Or going back a bit, Thelma and Louise? And just for fun - Lorde’s “Tennis Court” for the new Challengers movie!</p>
                                </div>
                            </Link>
                        </div>
                        <div className="discussion">
                            <Link to="/comment" className="discussion-link">
                                <div className="discussion-box">
                                    <h3 className="font1">Teatime & Trending Topics - April 23, 2024</h3>
                                    <p className="font1">In this thread you can discuss today's pop music gossip and trending topics. Acceptable content are rumors, tweets, gossip, and articles that would not be approved as its own post (e.g. not a legitimate news article or a social media post directly from the artist or their PR). Nudity and NSFW content is not accepted. War updates or political news without relation to celebrities is not allowed. While it is highly encouraged to link a source to any gossip or rumors you come across, it is not required and comments will not be removed if they do not do so.</p>
                                </div>
                            </Link>
                        </div>
                        <div className="discussion">
                            <Link to="/comment" className="discussion-link">
                                <div className="discussion-box">
                                    <h3 className="font1">Songs and Relatability</h3>
                                    <p className="font1">I'm older and grew up when music was a way for relaxation. So relatability has never been the primary reason for my listening to an artist or songs. Like how could I find relatability in Bon Jovi's Living on a Prayer as a teen still in school? Or more recently Ed Sheeran's The A Team as I'm not a sex worker or a drug addict. But I'm really moved by the lyrics of these songs every time I hear them. But like the majority of Taylor Swift's fans always cite accessibility and relatability as the main reason for their liking her. How about people here? Are those things important to you?</p>
                                </div>
                            </Link>
                        </div>
                        <div className="discussion">
                            <Link to="/comment" className="discussion-link">
                                <div className="discussion-box">
                                    <h3 className="font1">Billie Eilish is confirmed to be the next Headliner for Fortnite Festival</h3>
                                    <p className="font1">I’m super excited for this!</p>
                                </div>
                            </Link>
                        </div>
                    </section>
                    <section className="topcomsearch-column">
                        <div className="topsea">
                            <h4 className="font3">TOP SEARCHES</h4>
                            <a href="#" className="topsearches-link">
                                <h5 className="font">1# The Tortured Poets Album</h5>
                            </a>
                            <a href="#" className="topsearches-link">
                                <h5 className="font">2# New Releases</h5>
                            </a>
                            <a href="#" className="topsearches-link">
                                <h5 className="font">3# Coachella</h5>
                            </a>
                            <a href="#" className="topsearches-link">
                                <h5 className="font">4# Music Video</h5>
                            </a>
                            <a href="#" className="topsearches-link">
                                <h5 className="font">5# Tyler The Creator</h5>
                            </a>
                        </div>
                    </section>
                </section>
                <div className="add-discussion-position">
                    <button className="add-discussion" onClick={openModal}>
                        <p className="createpost">CREATE POST</p>
                        <span className="material-symbols-outlined">post_add</span>
                    </button>
                </div>
                <Link to="/home" className="homebutton">
                    <div className="add-discussion-position">
                        <button className="add-discussion">
                            <p className="createpost">HOME</p>
                            <span className="material-symbols-outlined">home</span>
                        </button>
                    </div>
                </Link>
                <Link to="/explore" className="explorebutton">
                    <div className="add-discussion-position">
                        <button className="add-discussion">
                            <p className="createpost">EXPLORE</p>
                            <span className="material-symbols-outlined">public</span>
                        </button>
                    </div>
                </Link>
                {isModalOpen && (
                    <div id="postModal" className="modal">
                        <div className="modal-content">
                            <h3>Create Post</h3>
                            <form id="postForm">
                                <label htmlFor="title">Title:</label><br />
                                <input type="text" id="title" name="title" /><br />
                                <label htmlFor="content">Content:</label><br />
                                <textarea id="content" name="content" rows="4" cols="50"></textarea><br />
                                <button type="submit">Create Post</button>
                                <button type="button" className="btn-cancel" onClick={closeModal}>Cancel</button>
                            </form>
                        </div>
                    </div>
                )}
            </main>

        </div>
    );
};

export default HomeForumPage;
