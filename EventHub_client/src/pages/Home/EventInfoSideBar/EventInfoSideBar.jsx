import { useState, useEffect, useRef } from 'react';
import styles from './EventInfoSideBar.module.css'
import { SlArrowLeft } from "react-icons/sl";
import { SlArrowRight } from "react-icons/sl";
import { MdOutlineDateRange } from "react-icons/md";
import { IoIosMore } from "react-icons/io";
import {getParticipants} from '../../../api/getParticipants';
import {getUserById} from '../../../api/getUserById';
import ParticipantsList from './ParticipantsList';

const EventInfoSideBar = ({event}) => {

    const [photoIndex, setPhotoIndex] = useState(0);
    const [participants, setParticipants] = useState([]);
    const [isShowMore, setIsShowMore] = useState(false);
    const [isOverflowAboutText, setIsOverflowAboutText] = useState(false);
    const [isShowMoreParticipants, setIsShowMoreParticipants] = useState(false);
    const [participantsToShow, setParticipantsToShow] = useState([]);
    const [showAllParticipants, setShowAllParticipants] = useState(false);
    const [owner, setOwner] = useState(null);

    const showMoreBtn = useRef(null);
    const aboutText = useRef(null);

    useEffect(() => {
        getParticipants(event.id)
        .then(data => {
            console.log('Data: ',data);
            setParticipants(data)
            if (data.length > 4) {
                setIsShowMoreParticipants(true);
                setParticipantsToShow(data.slice(0,4));
            }
            else {
                setParticipantsToShow(data);
            }

            if (aboutText.current.scrollHeight > aboutText.current.clientHeight) {
                setIsOverflowAboutText(true);
            }
        });
        
        
    }, [event]);

    useEffect(() => {
        getUserById(event.owner_id)
        .then(data => {
            setOwner(data);
            console.log(`Owner photo response: ${data.photo_responses[0].photo_url}`)
        })
    }, [event])

    useEffect(() => {
        console.log('participants to show: ',participantsToShow);
    }, [participantsToShow]);

    useEffect(() => {
        console.log('participants: ',participants);
    }, [participants]);


    const month = new Map();
    month.set('01', 'Jan');
    month.set('02', 'Feb');
    month.set('03', 'Mar');
    month.set('04', 'Apr');
    month.set('05', 'May');
    month.set('06', 'Jun');
    month.set('07', 'Jul');
    month.set('08', 'Aug');
    month.set('09', 'Sep');
    month.set('10', 'Nov');
    month.set('11', 'Oct');
    month.set('12', 'Dec');

    const handleRightPhotoClick = () => {
        if (photoIndex < event.photo_responses.length-1) {
            setPhotoIndex(prev => prev + 1);
        }
    }

    const handleLeftPhotoClick = () => {
        if (photoIndex > 0) {
            setPhotoIndex(prev => prev - 1);
        }
    }

    const handleShowMore = () => {
        setIsShowMore(prev => !prev);
        showMoreBtn.current.innerHTML = isShowMore ? 'Show more' : 'Show less';
    }

    

    return ( 
        <div className={styles['side-bar-container']}>
            <h2 className={styles['event-title']}>side-bar-container</h2>

            {/* Photo */}
            
                
                <div className={styles['photo-container']}>
                    <img className={styles['event-photo']} src={event.photo_responses[photoIndex].photo_url} alt="Event img" />
                    {event.photo_responses.length > 1 && <div className={styles['arrow-container']}>
                        <button onClick={handleLeftPhotoClick}> <SlArrowLeft /> </button>
                        <button onClick={handleRightPhotoClick}> <SlArrowRight /> </button>
                    </div>}
                 </div>
                 
                {/*(<img className={styles['event-photo']} src={event.photo_responses[0].photo_url} alt='Event img' />) */}
            

            {/* Category */}
            <div className={styles['category-container']}>
                {event.category_responses.map((category) => (
                    <div key={category.id} className={styles['category']}>{category.name}</div>
                ))}
            </div>

            {/* Date */}
            <div className={styles['date-container']}>
                <div className={styles['date-range-container']}>
                    <div className={styles['start-at']}>
                        <div className={styles['day']}>
                            {month.get(event.start_at.slice(5,7)) + ' ' + event.start_at.slice(8,10)}
                        </div>
                        <div className={styles['time']}>
                            {event.start_at.slice(11,16)}
                        </div>
                    </div>

                    <hr />

                    <div className={styles['expire-at']}>
                        <div className={styles['day']}>
                            {month.get(event.expire_at.slice(5,7)) + ' ' + event.expire_at.slice(8,10)}
                        </div>
                        <div className={styles['time']}>
                            {event.expire_at.slice(11,16)}
                        </div>
                    </div>
                </div>
                <div className={styles['date-icon']}>
                    <MdOutlineDateRange size='3em' />
                </div>
                
            </div>

            {/* Participants */}
            <h3 className={styles['heading']}>Participants</h3>
            <div className={styles['participant-container']}>
                <div className={styles['owner-photo']}>
                    {owner && <img src={owner.photo_responses[0].photo_url} alt="" />}
                </div>
                <div className={styles['participants-photos']}>
                    {participantsToShow.map(participant => (
                        <div className={styles['item']} key={participant.id}>
                            <img src={participant.participant_photo.photo_url} alt="Participant Img" />
                        </div>
                    ))}    
                    { isShowMoreParticipants && <div className={styles['show-more-participants']}><button onClick={() => setShowAllParticipants(!showAllParticipants)} ><IoIosMore /></button></div>}
                </div>
                
            </div>

            {/* About section */}
            <h3 className={styles['heading']}>About this event</h3>
            <div className={styles['about-container']}>
                <div className={styles[isShowMore ? 'about-text-more' : 'about-text']} ref={aboutText}>
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                Lorem, ipsum dolor sit amet consectetur adipisicing elit.
                    e.
                </div>
                {isOverflowAboutText && !isShowMore && <div className={styles['three-dots']}>...</div>}
                {isOverflowAboutText &&  <button onClick={handleShowMore} className={styles['show-more-btn']} ref={showMoreBtn}>Show more</button>}
            </div>

            {/* Lower section */}
            <div className={styles['lower-container']}>
                <div className={styles['spots']}>
                        {event.max_participants - event.participant_count} Spots left
                </div>

                <button className={styles['action-btn']}>Action</button>
            </div>

            {showAllParticipants && <ParticipantsList event={event} />}
            
        </div>
     );
}
 
export default EventInfoSideBar;