import CloseWindowButton from "../../../components/Buttons/CloseWindowButton/CloseWindowButton";
import CloseParticipantButton from "./CloseParticipantButton/CloseParticipantButton";
import GoBackButton from "../../../components/Buttons/GoBackButton/GoBackButton";
import styles from "./RequestsList.module.css";

import { deleteParticipant } from "../../../api/deleteParticipant";
import AcceptParticipantButton from "./AcceptParticipantButton/AcceptParticipantButton";
import { addParticipant } from "../../../api/addParticipant";
import SpotsLeft from "../../../components/Spots/SpotsLeft";
import { message } from "antd";
import { useNavigate } from "react-router-dom";
const RequestsList = ({
  _event,
  requests,
  handleGoBackToParticipantsList,
  handleCloseWindow,
  setReloadList,
}) => {
  const navigate = useNavigate();
  return (
    <div className={styles["requests-list-container"]}>
      <div className={styles["header"]}>
        <GoBackButton onClick={handleGoBackToParticipantsList} />
        <CloseWindowButton onClick={handleCloseWindow} />
      </div>
      <main>
        {requests.length === 0 && (
          <div className={styles["no-requests-msg"]}>
            Currently, there are no requests for this event..
          </div>
        )}

        <ul className={styles["requests-list"]}>
          {requests.map((requestedParticipant) => (
            <li
              className={styles["requested-participant-container"]}
              key={requestedParticipant.id}
            >
              <img
                onClick={() =>
                  navigate(`/profile/${requestedParticipant.username}`)
                }
                className={styles["requested-participant-photo"]}
                src={requestedParticipant.participant_photo.photo_url}
                alt="User requestedParticipant img"
              />
              <div className={styles["requested-participant-info-container"]}>
                <p className={styles["username"]}>
                  {`@${requestedParticipant.username}`}
                </p>
                <div className={styles["full-name"]}>
                  <p>{requestedParticipant.first_name}</p>
                  <p>{requestedParticipant.last_name}</p>
                </div>
              </div>
              <div className={styles["accept-requested-participant-container"]}>
                <AcceptParticipantButton
                  onClick={() => {
                    addParticipant(_event.id, requestedParticipant.id)
                      .then(() => {
                        setReloadList((prev) => !prev);
                      })
                      .catch((error) => {
                        if (error.response) {
                          const responseData = error.response.data;
                          if (
                            typeof responseData === "string" &&
                            responseData.includes("is full")
                          ) {
                            message.info("Event is full");
                          } else {
                            message.error("An error occurred");
                          }
                        } else {
                          message.error("An error occurred");
                        }
                      });
                  }}
                />
              </div>
              <div className={styles["delete-requested-participant-container"]}>
                <CloseParticipantButton
                  onClick={() => {
                    deleteParticipant(requestedParticipant.id, _event.id)
                      .then(() => setReloadList((prev) => !prev))
                      .catch((error) => message.error("An error occured"));
                  }}
                />
              </div>
            </li>
          ))}
        </ul>
      </main>

      <div className={styles["lower-container"]}>
        <SpotsLeft event={_event} />
      </div>
    </div>
  );
};

export default RequestsList;
