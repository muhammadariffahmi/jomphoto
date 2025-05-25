import React from 'react';

const discussions = [
    {
        title: "Taylor Swift - The Tortured Poets Department ALBUM REVIEW",
        content: `Reviews:
        Rolling Stone: 100/100 The Tortured Poets Department combines the intimacy of Folklore and Evermore with the synth-pop gloss of Midnights to create music that's wildly ambitious and gloriously chaotic.
        The Independent: 5/5 With its playful narratives and hooks like anchors, Swift’s 11th studio album is a terrific reminder of her storytelling powers.
        Variety: 94/100 Taylor Swift Renews Her Vows With Heartbreak in Audacious, Transfixing ‘Tortured Poets Department’
        The Irish Times: 4.5/5 This album is the fruit of abject misery but is also steeped in Swift’s trademark indefatigable optimism.`
    },
    {
        title: "What songs sound like they’d fit perfect for a movie soundtrack, even though they aren’t?",
        content: `Are there any songs throughout time, that you thought would sound really good for a specific movie (or TV series) despite not being associated with the film in any way?
        I thought “My Boy Only Breaks His Favorite Toys” by Taylor Swift would’ve been great for the Barbie movie, she even references Ken. It would’ve been a fitting song for when Ken has his tantrum!
        “II MOST WANTED” by Beyoncé & Miley would fit well in any sisterhood type movie, maybe Little Women? Or going back a bit, Thelma and Louise?
        And just for fun - Lorde’s “Tennis Court” for the new Challengers movie!`
    },
    {
        title: "Teatime & Trending Topics - April 23, 2024",
        content: `In this thread you can discuss today's pop music gossip and trending topics.
        Acceptable content are rumors, tweets, gossip, and articles that would not be approved as its own post
        (e.g. not a legitimate news article or a social media post directly from the artist or their PR).
        Nudity and NSFW content is not accepted. War updates or political news without relation to celebrities is not allowed.
        While it is highly encouraged to link a source to any gossip or rumors you come across, it is not required and comments will not be removed if they do not do so.`
    },
    {
        title: "Songs and Relatability",
        content: `I'm older and grew up when music was a way for relaxation. So relatability has never been the primary reason for my listening to an artist or songs. Like how could I find relatability in Bon Jovi's Living on a Prayer as a teen still in school? Or more recently Ed Sheeran's The A Team as I'm not a sex worker or a drug addict. But I'm really moved by the lyrics of these songs every time I hear them.
        But like the majority of Taylor Swift's fans always cite accessibility and relatability as the main reason for their liking her.
        How about people here? Are those things important to you?`
    },
    {
        title: "Billie Eilish is confirmed to be the next Headliner for Fortnite Festival",
        content: "I’m super excited for this!"
    }
];

const TopDiscussions = () => {
    return (
        <section>
            <h3 className="font2">TOP DISCUSSIONS</h3>
            {discussions.map((discussion, index) => (
                <div className="discussion" key={index}>
                    <a href="CommentPage.html" className="discussion-link">
                        <div className="discussion-box">
                            <h3 className="font1">{discussion.title}</h3>
                            <p className="font1">{discussion.content}</p>
                        </div>
                    </a>
                </div>
            ))}
            <div id="postOutput" className="newpost">
                {/* Display created posts here */}
            </div>
        </section>
    );
};

export default TopDiscussions;
